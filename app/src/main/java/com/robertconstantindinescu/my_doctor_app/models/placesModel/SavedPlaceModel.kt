package com.robertconstantindinescu.my_doctor_app.models.placesModel

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class SavedPlaceModel(
    var name:String?=null,
    var address:String?=null,
    var placeId:String?=null,
    var totalRating:Int?=null,
    var rating:Double?=null,
    var lat:Double?=null,
    var lng:Double?=null,
    var icon: String?=null,
    var vicinity: String?=null
):Parcelable
