package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import com.robertconstantindinescu.my_doctor_app.models.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class PatientToCallViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
)  : AndroidViewModel(application) {

    suspend fun getPatientsToCall() = repository.remote.getPatientsToCall()
}