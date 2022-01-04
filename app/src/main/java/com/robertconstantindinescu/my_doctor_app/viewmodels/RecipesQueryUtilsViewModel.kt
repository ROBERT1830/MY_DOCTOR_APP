package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import android.widget.Toast
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.robertconstantindinescu.my_doctor_app.models.DataStoreRepository
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.RecipesBottomSheet.Companion.vitaminAChecked
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.RecipesBottomSheet.Companion.vitaminCChecked
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.RecipesBottomSheet.Companion.vitaminDChecked
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.RecipesBottomSheet.Companion.vitaminEChecked

import com.robertconstantindinescu.my_doctor_app.utils.Constants
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_CUISINE_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_RECIPES_NUMBER
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_ADD_RECIPE_INFORMATION
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_API_KEY
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_CUISINE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_DIET
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_FILL_INGREDIENTS
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_NUMBER
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_VITAMIN_A
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_VITAMIN_C
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_VITAMIN_D
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_VITAMIN_E
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
    private var cuisineType = DEFAULT_CUISINE_TYPE
    private var vitaminA = Constants.DEFAULT_VITAMIN_A
    private var vitaminE = Constants.DEFAULT_VITAMIN_E
    private var vitaminC = Constants.DEFAULT_VITAMIN_C
    private var vitaminD = Constants.DEFAULT_VITAMIN_D


    val readBackOnline =
        dataStoreRepository.readBackOnline.asLiveData()

    var networkStatus = false
    var backOnline = false

    val readMealAndDietType = dataStoreRepository.readMealAndDietType

    fun saveMealAndDietType(
        mealType: String,
        mealTypeId: Int,
        dietType: String,
        dietTypeId: Int,
        cuisineType: String,
        cuisineTypeId: Int,
        vitaminA:String,
        vitaminAId:Int,
        vitaminE:String,
        vitaminEId:Int,
        vitaminC:String,
        vitaminCId:Int,
        vitaminD:String,
        vitaminDId:Int
    ) =
        //Dispatchers.IO pq vamos a hacer operaciones en base de datos auque sea mas pequeña como la datastore preference
        viewModelScope.launch(Dispatchers.IO) {
            dataStoreRepository.saveMealAndDietType(
                mealType,
                mealTypeId,
                dietType,
                dietTypeId,
                cuisineType,
                cuisineTypeId,
                vitaminA,
                vitaminAId,
                vitaminE,
                vitaminEId,
                vitaminC,
                vitaminCId,
                vitaminD,
                vitaminDId
            )
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
                cuisineType = value.selectedCuisineType
                vitaminA = value.selectedVitaminA
                vitaminE = value.selectedVitaminE
                vitaminC = value.selectedVitaminC
                vitaminD = value.selectedVitaminD

            }

        }


        //asociate the [key] with its value // all of them are in the query string http...
        queries[QUERY_CUISINE] = cuisineType

        if(vitaminAChecked) {
            queries[QUERY_VITAMIN_A] = vitaminA
        }
        if(vitaminEChecked){
            queries[QUERY_VITAMIN_E] = vitaminE
        }
        if(vitaminCChecked){
            queries[QUERY_VITAMIN_C] = vitaminC
        }
        if(vitaminDChecked){
            queries[QUERY_VITAMIN_D] = vitaminD
        }



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