package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import android.os.Parcelable
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
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
    val doctorModel: DoctorModel? = null,
    val time: String? = null,
    val patientId:String? = null,
    val doctorLiscence: String? = null,
    val doctorAppointmentKey: String? = null,
    val patientAppointmentKey: String? = null,
    val doctorFirebaseId: String? = null

):Parcelable