package com.robertconstantindinescu.my_doctor_app.interfaces

import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDataFirebaseModel

interface DetailsCancerDataInterface {
    fun onPatientCancerOutcomeClick(cancerDataFirebaseModel: CancerDataFirebaseModel)
}