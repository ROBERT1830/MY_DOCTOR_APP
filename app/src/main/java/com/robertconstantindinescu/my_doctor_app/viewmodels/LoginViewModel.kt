package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import com.robertconstantindinescu.my_doctor_app.models.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application) {

    fun login(email: String, password: String) = repository.remote.login(email, password)
}