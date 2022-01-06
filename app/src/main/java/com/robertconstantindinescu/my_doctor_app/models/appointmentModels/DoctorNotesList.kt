package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class DoctorNotesList(
    val doctorNotesList: ArrayList<CancerDoctorNote>
):Parcelable
