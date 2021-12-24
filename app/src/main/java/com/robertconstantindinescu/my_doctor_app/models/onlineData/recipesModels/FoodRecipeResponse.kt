package com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels


import com.google.gson.annotations.SerializedName

data class FoodRecipeResponse(
    @SerializedName("results")
    val results: List<Result>,
)