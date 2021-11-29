package com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class UVResponse(
    @SerializedName("current")
    val current: Current,
//    @SerializedName("lat")
//    val lat: Double,
//    @SerializedName("lon")
//    val lon: Double,
    @SerializedName("timezone")
    val timezone: String
): Parcelable