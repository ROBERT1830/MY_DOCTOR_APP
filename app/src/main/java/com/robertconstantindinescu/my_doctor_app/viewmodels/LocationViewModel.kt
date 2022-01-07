package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.robertconstantindinescu.my_doctor_app.models.Repository
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel
import com.robertconstantindinescu.my_doctor_app.models.placesModel.SavedPlaceModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class LocationViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) :
    AndroidViewModel(application) {

    fun getNearByPlace(url: String) = repository.remote.getPlaces(url)
    suspend fun getUserLocationId(): ArrayList<String> {

        //define a corroutine attatched to that viewModel
        return withContext(viewModelScope.coroutineContext) {
            val data = async {
                repository.remote.getUserLocationId()
            }
            data
        }.await()


    }

    fun removePlace(userSavedLocationId: ArrayList<String>) =
        repository.remote.removePlace(userSavedLocationId)

    fun addUserPlace(googlePlaceModel: GooglePlaceModel, userSavedLocationId: ArrayList<String>) =
        repository.remote.addUserPlace(googlePlaceModel, userSavedLocationId)

    fun getDirection(url: String) = repository.remote.getDirection(url)
    suspend fun getSavedPlaces(): ArrayList<GooglePlaceModel> = repository.remote.getSavedPlaces()


}