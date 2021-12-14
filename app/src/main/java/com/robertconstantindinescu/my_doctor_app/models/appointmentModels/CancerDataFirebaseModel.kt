package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import android.net.Uri
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CancerDataFirebaseModel (
    val cancerImg: String? =null,
    val date: String? = null,
    val outcome: String? = null
):Parcelable