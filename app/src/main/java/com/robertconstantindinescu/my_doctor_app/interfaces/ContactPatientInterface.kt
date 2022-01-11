package com.robertconstantindinescu.my_doctor_app.interfaces

import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel

interface ContactPatientInterface {
    fun onVideoCallClick(patientModel: PatientModel, position:Int)
    fun onDeleteCallClick(position: Int)


}