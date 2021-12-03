package com.robertconstantindinescu.my_doctor_app.models.loginUsrModels

data class PatientModel(
    var image: String? = null,
    var name: String? = null,
    var phoneNumber: String? = null,
    var email: String? = null,
    var isDoctor:Boolean? = null

)

