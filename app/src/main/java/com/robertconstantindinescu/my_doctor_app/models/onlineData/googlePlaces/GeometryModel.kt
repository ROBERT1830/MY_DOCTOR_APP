package com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel


import com.squareup.moshi.Json

data class GeometryModel(
    @field:Json(name = "location")
    val location: LocationModel?
)