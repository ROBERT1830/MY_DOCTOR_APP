package com.robertconstantindinescu.my_doctor_app.models.appointmentModels

import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel

data class AcceptedDoctorAppointmentModel(
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

)
