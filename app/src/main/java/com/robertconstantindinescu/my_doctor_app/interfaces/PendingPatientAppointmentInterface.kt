package com.robertconstantindinescu.my_doctor_app.interfaces


import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel

interface PendingPatientAppointmentInterface {

    fun onCancelButtonClick(pendingDoctorAppointmentModel: PendingPatientAppointmentModel)

}