package com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class LocationModel(
    @field:Json(name = "lat")
    val lat: Double?=null,

    @field:Json(name = "lng")
    val lng: Double?=null
):Parcelable