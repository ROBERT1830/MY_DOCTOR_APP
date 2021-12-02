package com.robertconstantindinescu.my_doctor_app.models

import android.net.Uri
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex.UVResponse
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.UvRadiationApi
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PROFILE_PATH
import com.robertconstantindinescu.my_doctor_app.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject


class RemoteDataSource @Inject constructor(private val uvRadiationApi: UvRadiationApi) {

    suspend fun getRadiationWeatherData(queries: Map<String, String>): Response<UVResponse> {
        return uvRadiationApi.getRadiationWeatherData(queries)
    }

    /** --LOGIN WITH FIREBASE-- */
    fun login(email: String, password: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))
        val auth = Firebase.auth

        val data = auth.signInWithEmailAndPassword(email, password).await()
        //if the task was performend in backend
        data?.let {
            if (auth.currentUser?.isEmailVerified!!) {
                emit(State.succes(R.string.login_succes)) //emiting the class directly by using the function
            } else {
                auth.currentUser?.sendEmailVerification()?.await()
                emit(State.failed(R.string.verify_email_first.toString()))
            }
        }
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    /** -- SIGN UP FIREBASE -- **/
    fun signUp(
        image: Uri,
        name: String,
        phoneNumber: String,
        email: String,
        password: String,
        doctorLiscence: String? = null,
        isDoctor: Boolean
    ): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val doctorModel: DoctorModel
        val patientModel: PatientModel

        val auth = Firebase.auth
        val data = auth.createUserWithEmailAndPassword(email, password).await()
        //could be a posibility that the user was not created.
        data.user?.let { currentUser ->
            val path = uploadImage(currentUser.uid, image).toString()
            if (isDoctor){
                doctorModel = createDoctorModel(path, name,phoneNumber , email, doctorLiscence, isDoctor)
                createDoctor(doctorModel , auth)
            }else {
                patientModel = createPatientModel(path, name,phoneNumber , email, isDoctor)
                createPatient(patientModel, auth)
            }
            emit(State.succes("Email verification sent"))



        }
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    private suspend fun createPatient(patientModel: PatientModel, auth: FirebaseAuth) {
        val firebase = Firebase.database.getReference("Users").child("Patients")
        firebase!!.child(auth.uid!!).setValue(patientModel).await()
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(patientModel.name)
            .setPhotoUri(Uri.parse(patientModel.image))
            .build()
        auth.currentUser?.apply {
            updateProfile(profileChangeRequest).await()
            sendEmailVerification().await()
        }
        
    }

    private suspend fun createDoctor(doctorModel: DoctorModel, auth: FirebaseAuth) {
        val firebase = Firebase.database.getReference("Users").child("Doctors")
        firebase!!.child(auth.uid!!).setValue(doctorModel).await()
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(doctorModel.name)
            .setPhotoUri(Uri.parse(doctorModel.image))
            .build()
        auth.currentUser?.apply {
            updateProfile(profileChangeRequest).await()
            sendEmailVerification().await()
        }
    }
    

    private fun createPatientModel(
        image: String,
        name: String,
        phoneNumber: String,
        email: String,
        isDoctor: Boolean
    ):PatientModel {

        return PatientModel( image, name,   phoneNumber, email, isDoctor)
    }

    private fun createDoctorModel(
        image: String,
        name: String,
        phoneNumber: String,
        email: String,
        doctorLiscence: String?,
        isDoctor: Boolean
    ):DoctorModel {
        return DoctorModel( image, name,   phoneNumber, email, doctorLiscence!!, isDoctor)


    }

    private suspend fun uploadImage(uid: String, image: Uri): Uri {

        val firebaseStorage = Firebase.storage
        val storageReference = firebaseStorage.reference
        //execute the task, wich is upload an imagge to firebaseStorage
        val task = storageReference.child(uid + PROFILE_PATH).putFile(image).await() //awais is a suspend function

        return task.storage.downloadUrl.await()
    }

    // TODO: 1/12/21 CONTINUE WITH THE REPO, LOGINVIEWMODEL AND VIEW.


}