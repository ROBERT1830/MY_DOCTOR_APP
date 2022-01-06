package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CancerDoctorNote(
    val cancerImg: String? =null,
    val note:String? = null
):Parcelable
