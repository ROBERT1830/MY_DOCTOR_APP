package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity

data class PendingDoctorAppointmentModel (
    val patientModel: PatientModel? = null,
    val cancerDataList: ArrayList<CancerDataEntity>? = null,
    val description: String? = null,
    val date: String? = null,
    val time: String? = null,
    val doctorAppointmentKey: String? = null,
    val patientAppointmentKey: String? = null,
    val appointmentStatus: String? = null
)