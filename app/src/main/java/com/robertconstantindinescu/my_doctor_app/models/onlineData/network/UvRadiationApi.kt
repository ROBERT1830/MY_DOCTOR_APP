package com.robertconstantindinescu.my_doctor_app.models.onlineData.network

import com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex.UVResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface UvRadiationApi {

    @GET("/data/2.5/onecall")
    suspend fun getRadiationWeatherData(
        @QueryMap queries: Map<String, String>
    ):Response<UVResponse>



}