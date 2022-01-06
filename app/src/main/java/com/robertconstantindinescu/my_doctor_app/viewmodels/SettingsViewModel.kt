package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import com.google.firebase.auth.AuthCredential
import com.robertconstantindinescu.my_doctor_app.models.Repository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val repo: Repository,
    application: Application
):AndroidViewModel(application){

    fun updateImage(image: Uri) = repo.remote.updateImage(image)

    fun updateName(name: String) = repo.remote.updateName(name)

    fun updateEmail(email: String) = repo.remote.updateEmail(email)

    fun updatePassword(password: String) = repo.remote.updatePassword(password)

    fun confirmEmail(authCredential: AuthCredential) = repo.remote.confirmEmail(authCredential)



}