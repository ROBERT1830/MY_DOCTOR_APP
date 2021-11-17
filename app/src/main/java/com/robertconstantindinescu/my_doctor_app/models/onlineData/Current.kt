package com.robertconstantindinescu.my_doctor_app.models.onlineData


import com.google.gson.annotations.SerializedName

data class Current(
    @SerializedName("uvi")
    val uvi: Double,
    @SerializedName("weather")
    val weather: List<Weather>
)