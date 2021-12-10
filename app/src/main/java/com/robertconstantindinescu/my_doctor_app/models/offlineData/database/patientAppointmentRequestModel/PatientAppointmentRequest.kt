package com.robertconstantindinescu.my_doctor_app.models.offlineData.database.patientAppointmentRequestModel

import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity

data class PatientAppointmentRequest (
    val patientModel: PatientModel? = null,
    val cancerDataList: ArrayList<CancerDataEntity>? = null,
    val description: String? = null,
    val date: String? = null,
    val time: String? = null,
    val doctorAppointmentKey: String? = null,
    val patientAppointmentKey: String? = null
)