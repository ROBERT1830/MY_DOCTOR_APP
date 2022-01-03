package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.*
import com.robertconstantindinescu.my_doctor_app.models.Repository
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.RecipesEntity
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.FoodRecipeResponse
import com.robertconstantindinescu.my_doctor_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class RecipesMainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) :
    AndroidViewModel(application) {

    /**
     * ROOM
     */
    val readRecipes: LiveData<List<RecipesEntity>> = repository.local.readRecipes()
        .asLiveData()  //we need to convert the flow to a live data. THIS


    /**
     * RETROFIT
     */
    var recipesResponse: MutableLiveData<NetworkResult<FoodRecipeResponse>> = MutableLiveData()
    fun getRecipes(queries: Map<String, String>) = viewModelScope.launch {
        getRecipesSafeCall(queries)
    }

    private suspend fun getRecipesSafeCall(queries: Map<String, String>) {
        //every time we call this funtion we response first with the load state data and then we we get the data we responthe either with succes o error.
        recipesResponse.value = NetworkResult.Loading()
        //si tenemos internet we will make a request to our api. And we wil store the request response inside
        //the recipesResponse variable
        if (hasInternerConnection()) {
            try {

                val response = repository.remote.getRecipes(queries)
                recipesResponse.value =
                    handleFoodRecipesResponse(response) //si recordamos del Succes devuevle un Network con un tipo <T> con lo cual gracias a eso podemos igualar NetworkResult<FoodRecipe>  al tipo de dato que tenemos devuelto
                /**Catche the data inmediatly when we receive from api. */
                val foodRecipe =
                    recipesResponse.value!!.data //accedes al Network que tiene almacenado y almacenas en sa varible la respuesta que es realmete un objeto de la clase FoodRecipes
                if (foodRecipe != null) { //chek if the response is not null
                    offlineCacheRecipes(foodRecipe) //cache the api response into the database.
                }


            } catch (e: Exception) {
                recipesResponse.value = NetworkResult.Error("Recipes not found. ")
            }
            //if has internet coneection is false that menas tha toour app dont have aacces to intener and we need to pass error emsage here
        } else {
            //if there is an error we are seting the mutable recupesReposnse to the networkresult  error
            recipesResponse.value =
                NetworkResult.Error("No Internet Connection.") //here we need to pass some parameters lke the message but not the data becasue we seted to null in case of error
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternerConnection(): Boolean {
        /*to check if the app has internet conection firts we get the application class
        * and the getSystemService return A ConnectivityManager for handling management of network connections.*/
        //
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork
            ?: return false // activeNetwork--->Returns a Network (si tienes coenxion) object corresponding to the currently active default data network This will return null when there is no default network.
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return false  // lo que hace es coger e identificar la capacidad de red, es decir si hay o no capacidad de tner red con el objeto network que identifica la red y que pasamos cmo parmetro. Si con esa red de internet no tenemos capacidad de conexion devuelveme falso.
        //se coge ee objeto networ y se usa pra ver que tipo de conexxion estamos teniendo
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false


        }
    }

    private fun handleFoodRecipesResponse(response: Response<FoodRecipeResponse>): NetworkResult<FoodRecipeResponse>? {
        when {

            response.message().toString().contains("timeout") -> {
                return NetworkResult.Error("Timeout")
            }
            response.code() == 402 -> {
                return NetworkResult.Error("Api key Limited. ")
            }
            response.body()!!.results.isNullOrEmpty() -> {
                return NetworkResult.Error("Recipes not found.")


            }
            response.isSuccessful -> {
                val foodRecipes = response.body()
                return NetworkResult.Success(foodRecipes!!)
            }
            else -> {
                return NetworkResult.Error(response.message())
            }
        }


    }

    private fun offlineCacheRecipes(foodRecipe: FoodRecipeResponse) {
        //aqui te vas a crear tu propio objeto de la clase ReipesEntity que contiene dentro la clase FoodRecipe con la lista de result
        //y le meteras los resulatdos pq le pasas por constructor un objeto de la clase FoodRecipe
        /**
         * in order to insert the data into the database we need to convert the FoodRecipe from the api
         * into an database entity in that case recipesEntity
         */
        val recipesEntity =
            RecipesEntity(foodRecipe) //entonces esta variable contiene un objeto de la clase RecipeseNTTITY que contiene dentro un objeto de tipo FooDrecipe que a asu vez tiene una lista de Result
        //ahora vamos a insertar los datos en room
        insertRecipes(recipesEntity)
    }

    private fun insertRecipes(recipesEntity: RecipesEntity) =
        //we use the dispatcher io becasue we perform operation with database
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertRecipes(recipesEntity)
        }

}