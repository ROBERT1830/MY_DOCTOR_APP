package com.robertconstantindinescu.my_doctor_app.utils

import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.CuisineTypeModel
import com.robertconstantindinescu.my_doctor_app.models.placesModel.PlaceModel

class Constants {

    companion object{


        //Image from gallery
        const val STORAGE_REQUEST_CODE = 1000
        @JvmStatic
        val PROFILE_PATH="/Profile/image_profile.jpg"
        val CANCER_PATH="Cancer/"

        //RETROFIT GOOGLE_API
        const val GOOGLE_MAP_BASE_URL = "https://maps.googleapis.com/"
        //google api key we have it in string


        //RETROFIT RADIATION_API
        const val BASE_URL = "https://api.openweathermap.org"
        const val API_KEY = "b244b85b8c90315f903c2c636faf93de"
        const val QUERY_LATITUDE = "lat"
        const val QUERY_LONGITUDE = "lon"
//        const val QUERY_EXCLUDE = "exclude"
        const val QUERY_API = "appid"



        //Room DATABSE
        //-->cancerDb
        const val DATABSE_NAME = "user_melanoma_database"
        const val CANCER_DATA_TABLE = "cancer_data_table"
        const val RADIATION_WEATHER_TABLE = "radiation_weather_table"
        //-->RecipesDb
        const val RECIPES_TABLE = "recipes_table"



        //Chip Group elements
        //-->GoolgeMaps
        var placesName = listOf<PlaceModel>(
            PlaceModel(1, R.drawable.ic_pharmacy, "Pharmacies", "pharmacy"),
            PlaceModel(2, R.drawable.ic_hospital, "Hospitals", "hospital")
        )
        //-->Recipes Cuisine chip types.
        var cuisineTypes = listOf<CuisineTypeModel>(
            CuisineTypeModel(1, R.drawable.chinese, "chinese"),
            CuisineTypeModel(1, R.drawable.japanise, "japanese"),
            CuisineTypeModel(1, R.drawable.spanish, "spanish"),
            CuisineTypeModel(1, R.drawable.turquia, "turkish"),
            CuisineTypeModel(1, R.drawable.morroco, "moroccan"),
            CuisineTypeModel(1, R.drawable.mexico, "mexican"),
            CuisineTypeModel(1, R.drawable.italian, "italian"),
            CuisineTypeModel(1, R.drawable.indian, "indian"),

        )



        //DETAIL PATIENT APPOINTMENT
        const val PENDING_DOCTOR_APPOINTMENT_MODEL = "pending_doctor_appointment_model"

        //Doctor appointment
        const val CANCER_DATA = "cancerData"
        const val FROM_SAVE_DOCTOR_NOTES = "fromSaveDoctorNotes"

        const val OPTION_1 = "\n" +
                "No need to worry. We will follow the evolution of the mole in future appointments"

        const val OPTION_2 ="I have got an unexpected situation"
        var doctorCancelChoice = arrayOf(OPTION_1, OPTION_2)

        //Notification
        const val NOTIFICATION_BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY= "AAAA_lthKLk:APA91bHN3aFgxXEHCnyT_bGDNUYF0Z8VUNacO3LxY3WU9MlydBleOm2oTCSgukqF70KB72EingFfh7qkWMCNRXnGo1QJUDxd4LvYZCjjdVQKIToqNdSSXOO4-qFtUqrm8AD_NSeYPJnW"
        const val CONTENT_TYPE = "application/json"

        //video call
        const val ROOM_CODE = "room_code"
        const val APP_ID = "4f093c6ebdcc4f8fafe489d9060c151c"




        //RECIPES QUERY KEYS
        const val RECIPE_BASE_URL = "https://api.spoonacular.com"
        const val BASE_IMAGE_URL ="https://spoonacular.com/cdn/ingredients_100x100/"
        const val RECIPE_API_KEY = "fc9a88b126ac432786da2b6181c073d5"



        const val QUERY_NUMBER = "number"
        const val QUERY_API_KEY = "apiKey"
        const val QUERY_TYPE = "type"
        const val QUERY_DIET = "diet"
        const val QUERY_CUISINE = "cuisine"
        const val QUERY_VITAMIN_A = "maxVitaminA"
        const val QUERY_VITAMIN_E = "maxVitaminE"
        const val QUERY_VITAMIN_C = "maxVitaminC"
        const val QUERY_VITAMIN_D = "maxVitaminD"
        const val QUERY_ADD_RECIPE_INFORMATION = "addRecipeInformation"
        const val QUERY_FILL_INGREDIENTS = "fillIngredients"


        //BootomSheet for ingredients
        const val DEFAULT_RECIPES_NUMBER = "50"
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"
        const val DEFAULT_CUISINE_TYPE = "italian"
        const val DEFAULT_VITAMIN_A = "500"
        const val DEFAULT_VITAMIN_E = "50"
        const val DEFAULT_VITAMIN_D = "100"
        const val DEFAULT_VITAMIN_C = "50"

        const val PREFERENCES_NAME = "foody_preferences" //this is hte namem of the datastore prefernece and under that name all other values will be store
        const val PREFERENCES_MEAL_TYPE = "mealType"
        const val PREFERENCES_MEAL_TYPE_ID = "mealTypeId"
        const val PREFERENCES_DIET_TYPE = "dietType"
        const val PREFERENCES_DIET_TYPE_ID = "dietTypeId"
        const val PREFERENCES_CUISINE_TYPE = "cuisineType"
        const val PREFERENCES_CUISINE_TYPE_ID = "cuisineTypeId"
        const val PREFERENCES_VITAMIN_A = "vitaminA"
        const val PREFERENCES_VITAMIN_A_ID = "vitaminAId"
        const val PREFERENCES_VITAMIN_E = "vitaminE"
        const val PREFERENCES_VITAMIN_E_ID = "vitaminEId"
        const val PREFERENCES_VITAMIN_C = "vitaminC"
        const val PREFERENCES_VITAMIN_C_ID = "vitaminCId"
        const val PREFERENCES_VITAMIN_D = "vitaminD"
        const val PREFERENCES_VITAMIN_D_ID = "vitaminDId"

        //Preferences Internet
        const val PREFERENCES_BACK_ONLINE = "backOnline"


    }

}