package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.robertconstantindinescu.my_doctor_app.models.data.Repository
import com.robertconstantindinescu.my_doctor_app.models.data.database.entities.CancerDataEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
): AndroidViewModel(application){

    //Variable para leer de la base de datos (tabla cancerEntity)
    val readCancerDataEntity: LiveData<List<CancerDataEntity>> = repository.local.readCancerData().asLiveData()

    fun insertCancerRecord(cancerDataEntity: CancerDataEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.insertCancerRecord(cancerDataEntity)
    }

    fun deleteCancerRecord(cancerDataEntity: CancerDataEntity) = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteCancerRecord(cancerDataEntity)
    }

    fun deleteAllCancerRecords() = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteAllCancerRecords()
    }


}