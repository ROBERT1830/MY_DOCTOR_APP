package com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel


import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class GeometryModel(
    @field:Json(name = "location")
    val location: LocationModel?
): Parcelable