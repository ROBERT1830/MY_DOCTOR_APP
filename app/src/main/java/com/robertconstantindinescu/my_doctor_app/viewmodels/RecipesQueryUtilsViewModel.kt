package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.robertconstantindinescu.my_doctor_app.models.DataStoreRepository
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.API_KEY
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_API_KEY
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_DIET
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_NUMBER
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RECIPE_API_KEY
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecipesQueryUtilsViewModel @Inject constructor(
    application: Application,
    private val dataStoreRepository: DataStoreRepository
) :
    AndroidViewModel(application) {

    private var mealType = DEFAULT_MEAL_TYPE
    private var dietType = DEFAULT_DIET_TYPE

    val readBackOnline =
        dataStoreRepository.readBackOnline.asLiveData()

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    fun saveMealAndDietType(mealType: String, mealTypeId: Int, dietType: String, dietTypeId: Int) =
        //Dispatchers.IO pq vamos a hacer operaciones en base de datos auque sea mas pequeña como la datastore preference
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(mealType, mealTypeId, dietType, dietTypeId)
        }

    fun saveBackOnline(backOnline: Boolean) =
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveBackOnline(backOnline)
        }



    fun showNetworkStatus() {
        if (!networkStatus) {
            Toast.makeText(getApplication(), "No internet Connection.", Toast.LENGTH_SHORT).show()
            //when we lose our internet conenction and we want to set the value of that function to tru.
            saveBackOnline(true)
        } else if (networkStatus) {
            if (backOnline) {
                Toast.makeText(getApplication(), "We are back online", Toast.LENGTH_SHORT).show()
                saveBackOnline(false)
            }
        }
    }

    fun applyQueries(): HashMap<String, String> {
        val queries: HashMap<String, String> = HashMap()

        /**launcha coroutine to collect the values from that particular flow. The flow is the comunication or read action with
         * the datasource
         * el colelct te recoge este dato del flow que es un MealAndDietType*/
        viewModelScope.launch {
            readMealAndDietType.collect { value ->
                /**Hasta que no se coleccionan los datos no se vuelve a emitir uno. Es decir el flow se queda suspendido epseranod a que su dato emitido sea coleecioando*/
                //now store the value in both up variables
                //esta d variabels van a toamr el valor de la datastore preference repo directametne
                //y ahora vamos a coger y usar esas variables en el hasmap del query
                mealType = value.selectedMealType
                dietType = value.selectedDietType

            }

        }


        //asociate the [key] with its value // all of them are in the query string http...
        queries["cuisine"] = "italian"
        queries["maxVitaminA"] = "50"
        queries["maxVitaminC"] = "50"
        queries["maxVitaminD"] = "50"
        queries["maxVitaminE"] = "50"

        queries[QUERY_NUMBER] =
            DEFAULT_RECIPES_NUMBER  //this is the number of recipes we will get from the request
        queries[QUERY_API_KEY] = RECIPE_API_KEY
        //her ewe will get the new values for the endpoints and if there is no data inside that variable we will perform a default value because we specicy it in the .map inside the datastore rEPOSITORY
        queries[QUERY_TYPE] = mealType
        queries[QUERY_DIET] = dietType

        queries[QUERY_ADD_RECIPE_INFORMATION] = "true"
        queries[QUERY_FILL_INGREDIENTS] = "true"
        return queries
    }

}