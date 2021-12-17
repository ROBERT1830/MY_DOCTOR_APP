package com.robertconstantindinescu.my_doctor_app.models.loginUsrModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PatientModel(
    var image: String? = null,
    var patientName: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var isDoctor:Boolean? = null,
    var firebaseId: String? = null

): Parcelable

