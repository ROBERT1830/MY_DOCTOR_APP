package com.robertconstantindinescu.my_doctor_app.models.loginUsrModels

data class PatientModel(
    var image: String = "",
    var name: String = "",
    var phoneNumber: String = "",
    var email: String = "",
    var isDoctor:Boolean
)

