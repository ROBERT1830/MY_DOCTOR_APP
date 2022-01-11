package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.robertconstantindinescu.my_doctor_app.models.Repository
import com.robertconstantindinescu.my_doctor_app.models.notificationModels.NotificationDataModel
import com.robertconstantindinescu.my_doctor_app.models.notificationModels.PushNotificationModel
import com.robertconstantindinescu.my_doctor_app.utils.NetworkResult
import com.robertconstantindinescu.my_doctor_app.utils.State
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PatientToCallViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
)  : AndroidViewModel(application) {

    private val _notificationData = MutableLiveData<State<Any>>()
    val notificationData: LiveData<State<Any>>
        get() = _notificationData


    suspend fun getPatientsToCall() = repository.remote.getPatientsToCall()



    fun sendNotificationToPatient(notification: PushNotificationModel){
        viewModelScope.launch(Dispatchers.IO){
            getDataSafeCall(notification)
        }
    }

    private suspend fun getDataSafeCall(notification: PushNotificationModel) {

        // TODO: 21/12/21 check for internet coneection

        try {
            val response = repository.remote.sendNotificationToPatient(notification)
            if (response.isSuccessful){
                _notificationData.postValue(State.succes("Notification sent successfully!"))

            }


        }catch (e: Exception){
            _notificationData.postValue(State.failed("Notification failed"))

        }

    }

    suspend fun deletePatientCalled(position: Int) = repository.remote.deletePatientCalled(position)

    //fun sendNotificationToPatient(notification: PushNotificationModel) = repository.remote.sendNotificationToPatient(notification)

}