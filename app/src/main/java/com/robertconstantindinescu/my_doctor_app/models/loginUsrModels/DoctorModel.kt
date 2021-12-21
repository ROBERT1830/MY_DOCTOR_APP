package com.robertconstantindinescu.my_doctor_app.models.loginUsrModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DoctorModel(
    var image: String? = null,
    var doctorName: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var doctorLiscence: String? = null,
    var isDoctor:Boolean? = null,
    var firebaseId: String? = null,
    var doctorAppToken: String? = null
): Parcelable
