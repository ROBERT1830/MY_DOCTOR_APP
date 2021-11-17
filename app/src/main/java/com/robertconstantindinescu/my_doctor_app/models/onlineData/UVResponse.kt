package com.robertconstantindinescu.my_doctor_app.models.onlineData


import com.google.gson.annotations.SerializedName

data class UVResponse(
    @SerializedName("current")
    val current: Current,
    @SerializedName("lat")
    val lat: Double,
    @SerializedName("lon")
    val lon: Double,
    @SerializedName("timezone")
    val timezone: String
)