package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.robertconstantindinescu.my_doctor_app.models.Repository
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RequestAppointmentViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    fun createPendingDoctorPatientAppointment(
        doctorModel: DoctorModel,
        cancerList: MutableList<CancerDataEntity>, description: String, date: String, time:String
    ) =
        repository.remote.createPendingDoctorPatientAppointment(doctorModel, cancerList, description, date, time)


}