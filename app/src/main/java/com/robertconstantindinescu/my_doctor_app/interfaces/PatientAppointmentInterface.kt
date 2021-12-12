package com.robertconstantindinescu.my_doctor_app.interfaces

import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel

interface PatientAppointmentInterface {

    fun onCancelButtonClick(patientAppointmentModel: PendingPatientAppointmentModel)
}