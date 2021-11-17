package com.robertconstantindinescu.my_doctor_app.models

import com.robertconstantindinescu.my_doctor_app.models.onlineData.UVResponse
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.UvRadiationApi
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSource @Inject constructor(private val uvRadiationApi: UvRadiationApi){

    suspend fun getRadiationWeatherData(queries: Map<String, String>): Response<UVResponse>{
        return uvRadiationApi.getRadiationWeatherData(queries)
    }
}