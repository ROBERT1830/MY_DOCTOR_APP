package com.robertconstantindinescu.my_doctor_app.utils

import com.robertconstantindinescu.my_doctor_app.R
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



        //GoolgeMaps
        var placesName = listOf<PlaceModel>(
            PlaceModel(1, R.drawable.ic_pharmacy, "Pharmacies", "pharmacy"),
            PlaceModel(2, R.drawable.ic_hospital, "Hospitals", "hospital")
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


        //BootomSheet for ingredients
        const val DEFAULT_RECIPES_NUMBER = "50"
        const val DEFAULT_MEAL_TYPE = "main course"
        const val DEFAULT_DIET_TYPE = "gluten free"

        const val PREFERENCES_NAME = "foody_preferences" //this is hte namem of the datastore prefernece and under that name all other values will be store
        const val PREFERENCES_MEAL_TYPE = "mealType"
        const val PREFERENCES_MEAL_TYPE_ID = "mealTypeId"
        const val PREFERENCES_DIET_TYPE = "dietType"
        const val PREFERENCES_DIET_TYPE_ID = "dietTypeId"

        //Preferences Internet
        const val PREFERENCES_BACK_ONLINE = "backOnline"


    }

}