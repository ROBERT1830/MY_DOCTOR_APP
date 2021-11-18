package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.*
import com.robertconstantindinescu.my_doctor_app.models.Repository
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.UVEntity
import com.robertconstantindinescu.my_doctor_app.models.onlineData.UVResponse
import com.robertconstantindinescu.my_doctor_app.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Response
import java.lang.Exception
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    //Variable para leer de la base de datos (tabla cancerEntity)
    /**ROOM LIVEDATA*/

    val readCancerDataEntity: LiveData<List<CancerDataEntity>> =
        repository.local.readCancerData().asLiveData()
//    val readRadiationWaetherData: LiveData<UVEntity> =
//        repository.local.readRadiationWeatherData().asLiveData()

    /**RETROFIT LIVEDATA*/
    private val _radiationWeatherDataResponse = MutableLiveData<NetworkResult<UVResponse>>()
    val radiationWeatherDataResponse: LiveData<NetworkResult<UVResponse>>
        get() = _radiationWeatherDataResponse

    /**ROOM*/
    fun insertCancerRecord(cancerDataEntity: CancerDataEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.insertCancerRecord(cancerDataEntity)
        }

    fun deleteCancerRecord(cancerDataEntity: CancerDataEntity) =
        viewModelScope.launch(Dispatchers.IO) {
            repository.local.deleteCancerRecord(cancerDataEntity)
        }

    fun deleteAllCancerRecords() = viewModelScope.launch(Dispatchers.IO) {
        repository.local.deleteAllCancerRecords()
    }

    /**RETROFIT*/
    @RequiresApi(Build.VERSION_CODES.M)
    fun getRadiationWeatherData(queries: Map<String, String>) =
        viewModelScope.launch(Dispatchers.IO) {
            getdataSafeCall(queries)
        }

    @RequiresApi(Build.VERSION_CODES.M)
    private suspend fun getdataSafeCall(queries: Map<String, String>) {

        _radiationWeatherDataResponse.value = NetworkResult.Loading()
        if (hasInternetConnection()) {
            try {
                //obtenemos la respuesta del servidor
                val response = repository.remote.getRadiationWeatherData(queries)
                _radiationWeatherDataResponse.value = handleRadiationWeatherResponse(response)
                //val radiationWeatherData = radiationWeatherDataResponse.value!!.data
//                if (radiationWeatherData != null) {
//                    offlineCahceRadiationData(radiationWeatherData)
//                }
            } catch (e: Exception) {
                _radiationWeatherDataResponse.value = NetworkResult.Error(
                    "Radiation and weather data" +
                            "not found"
                )
            }

        } else _radiationWeatherDataResponse.value = NetworkResult.Error("No Internet Connection")

    }

//    private fun offlineCahceRadiationData(radiationWeatherData: UVResponse) {
//        val uvEntity = UVEntity(radiationWeatherData)
//        insertRadiationWeatherData(uvEntity)
//
//    }
//
//    private fun insertRadiationWeatherData(uvEntity: UVEntity) =
//        viewModelScope.launch(Dispatchers.IO) {
//            repository.local.insertRadiationWeatherData(uvEntity)
//        }

    private fun handleRadiationWeatherResponse(response: Response<UVResponse>): NetworkResult<UVResponse>? {

        when {
            response.code() == 402 -> {
                return NetworkResult.Error("Api Key Limited")
            }
            response.code() == 404 -> {
                return NetworkResult.Error("the format of your API request is incorrect")

            }
            response.body() == null -> {
                return NetworkResult.Error("Recipes not found.")
            }
            response.isSuccessful -> {
                val radiationWeatherData = response.body()
                return NetworkResult.Success(radiationWeatherData!!)
            }
            else -> {
                return NetworkResult.Error(response.message()) //get the messge from the api
            }
        }

    }

    @RequiresApi(Build.VERSION_CODES.M)
    private fun hasInternetConnection(): Boolean {
        //vamos a la application class, cogemos el manejador de conexion del ssitema.
        val connectivityManager = getApplication<Application>().getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork) ?: return false
        return when {
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
            capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
            else -> false
        }
    }


}