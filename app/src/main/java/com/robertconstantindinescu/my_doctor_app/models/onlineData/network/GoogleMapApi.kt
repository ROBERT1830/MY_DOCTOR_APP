package com.robertconstantindinescu.my_doctor_app.models.onlineData.network

import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GoogleResponseModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Url

interface GoogleMapApi {

    @GET  //con el metodo url
    suspend fun getNearByPlaces(@Url url: String): Response<GoogleResponseModel>


}