package com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels


import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Nutrition(
    @SerializedName("nutrients")
    val nutrients: List<Nutrient>?
): Parcelable