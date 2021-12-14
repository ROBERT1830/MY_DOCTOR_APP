package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import android.os.Parcelable
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PendingDoctorAppointmentModel (
    val appointmentStatus: String? = null,
    val cancerDataList: ArrayList<CancerDataFirebaseModel>? = null,
    val date: String? = null,
    val description: String? = null,
    val patientModel: PatientModel? = null,
    val time: String? = null,
    val doctorAppointmentKey: String? = null,
    val patientAppointmentKey: String? = null,

):Parcelable