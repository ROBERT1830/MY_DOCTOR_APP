package com.robertconstantindinescu.my_doctor_app.models.loginUsrModels

data class DoctorModel(
    var image: String = "",
    var name: String = "",
    var phoneNumber: String = "",
    var email: String = "",
    var doctorLiscence: String = "",
    var isDoctor:Boolean
)
