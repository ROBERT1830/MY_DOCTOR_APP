package com.robertconstantindinescu.my_doctor_app.models

import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex.UVResponse
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.UvRadiationApi
import com.robertconstantindinescu.my_doctor_app.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import okhttp3.Dispatcher
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSource @Inject constructor(private val uvRadiationApi: UvRadiationApi){

    suspend fun getRadiationWeatherData(queries: Map<String, String>): Response<UVResponse>{
        return uvRadiationApi.getRadiationWeatherData(queries)
    }

    /** --LOGIN WITH FIREBASE-- */
    fun login(email: String, password: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))
        val auth = Firebase.auth

        val data = auth.signInWithEmailAndPassword(email, password).await()
        //if the task was performend in backend
        data?.let {
            if(auth.currentUser?.isEmailVerified!!){
                emit(State.succes(R.string.login_succes)) //emiting the class directly by using the function
            }else{
                auth.currentUser?.sendEmailVerification()?.await()
                emit(State.failed(R.string.verify_email_first.toString()))
            }
        }
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)
}