package com.robertconstantindinescu.my_doctor_app.models.onlineData.network

import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.FoodRecipeResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.QueryMap

interface FoodRecipeApi {

    @GET("/recipes/complexSearch")
    suspend fun getRecipes(
        @QueryMap queries: Map<String, String>
    ): Response<FoodRecipeResponse>
}