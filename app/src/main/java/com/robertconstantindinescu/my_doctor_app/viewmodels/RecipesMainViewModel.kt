package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class RecipesMainViewModel @Inject constructor(application: Application) :
    AndroidViewModel(application) {
}