package com.robertconstantindinescu.my_doctor_app.utils

class Constants {

    companion object{


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


    }

}