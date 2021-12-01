package com.robertconstantindinescu.my_doctor_app.utils

import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.placesModel.PlaceModel

class Constants {

    companion object{


        //Image from gallery
        const val STORAGE_REQUEST_CODE = 1000


        //RETROFIT RADIATION_API
        const val BASE_URL = "https://api.openweathermap.org"
        const val API_KEY = "b244b85b8c90315f903c2c636faf93de"
        const val QUERY_LATITUDE = "lat"
        const val QUERY_LONGITUDE = "lon"
//        const val QUERY_EXCLUDE = "exclude"
        const val QUERY_API = "appid"



        //Room DATABSE
        const val DATABSE_NAME = "user_melanoma_database"
        const val CANCER_DATA_TABLE = "cancer_data_table"
        const val RADIATION_WEATHER_TABLE = "radiation_weather_table"


        //GoolgeMaps
        var placesName = listOf<PlaceModel>(
            PlaceModel(1, R.drawable.ic_pharmacy, "Pharmacies", "pharmacy"),
            PlaceModel(2, R.drawable.ic_hospital, "Hospitals", "hospital")
        )


    }

}