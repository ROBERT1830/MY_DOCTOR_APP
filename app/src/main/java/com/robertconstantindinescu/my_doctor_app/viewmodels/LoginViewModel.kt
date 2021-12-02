package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.robertconstantindinescu.my_doctor_app.models.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    fun login(email: String, password: String) = repository.remote.login(email, password)
    fun signUp(
        image: Uri,
        name: String,
        phoneNumber: String,
        email: String,
        password: String,
        doctorLiscence: String? = null,
        isDoctor: Boolean
    ) =
        repository.remote.signUp(
            image,
            name,
            phoneNumber,
            email,
            password,
            doctorLiscence,
            isDoctor
        )

}