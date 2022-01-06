package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import android.os.Parcelable
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PendingPatientAppointmentModel(


    val appointmentDate: String? = null,
    val appointmentDescription: String? = null,
    val appointmentStatus: String? = null,
    val appointmentTime: String? = null,
    val doctorAppointmentKey: String? = null,
    val doctorImage: String? = null,
    val doctorName: String? = null,
    val patientAppointmentKey: String? = null,
    val doctorId: String? = null,
    val doctorFirebaseId: String? = null,
    val patientFirebaseId: String? = null,

):Parcelable
