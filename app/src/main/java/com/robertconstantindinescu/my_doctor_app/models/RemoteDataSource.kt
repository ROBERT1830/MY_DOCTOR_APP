package com.robertconstantindinescu.my_doctor_app.models

import android.content.ContentValues.TAG
import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.*
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.ktx.storage
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.*
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.notificationModels.PushNotificationModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.FoodRecipeApi
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.GoogleMapApi
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.NotificationApi
import com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex.UVResponse
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.UvRadiationApi
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.FoodRecipeResponse
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.CANCER_PATH
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PROFILE_PATH
import com.robertconstantindinescu.my_doctor_app.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.ByteArrayOutputStream
import javax.inject.Inject
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase


class RemoteDataSource @Inject constructor(
    private val uvRadiationApi: UvRadiationApi,
    private val googleMapApi: GoogleMapApi,
    private val notificationApi: NotificationApi,
    private val foodRecipeApi: FoodRecipeApi
) {

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
                emit(State.succes("Login Succesfully")) //emiting the class directly by using the function

            } else {
                auth.currentUser?.sendEmailVerification()?.await()
                emit(State.failed(R.string.verify_email_first.toString()))
            }
        }

    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    /** -- SIGN UP FIREBASE -- **/


    /**
     * ----------------------->
     */
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

        data.user?.let { currentUser ->
            //val appToken = createAppToken()
            val path = uploadImage(currentUser.uid, image).toString()
            if (isDoctor) {
                doctorModel =
                    createDoctorModel(
                        path,
                        name,
                        phoneNumber,
                        email,
                        doctorLiscence,
                        isDoctor,
                        auth.uid!!,
                        //appToken
                    )
                createDoctor(doctorModel, auth)
            } else {
                patientModel =
                    createPatientModel(
                        path,
                        name,
                        phoneNumber,
                        email,
                        isDoctor,
                        auth.uid!!, /*appToken*/
                    )
                createPatient(patientModel, auth)
            }
            emit(State.succes("Email verification sent"))

        }
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    // TODO: 20/12/21 ---------->
    private fun createAppToken(): String {


        var token = ""
        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.w(TAG, "Fetching FCM registration token failed", task.exception)
                return@addOnCompleteListener
            }
            token = task.result
            Log.d("token", token.toString())

        }
        return token

    }

    private suspend fun createPatient(patientModel: PatientModel, auth: FirebaseAuth) {
        val firebase = Firebase.database.getReference("Users")//.child("Patients")
        firebase.child(auth.uid!!).setValue(patientModel).await()
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(patientModel.patientName)
            .setPhotoUri(Uri.parse(patientModel.image))
            .build()
        auth.currentUser?.apply {
            updateProfile(profileChangeRequest).await()
            sendEmailVerification().await()
        }

    }

    private suspend fun createDoctor(doctorModel: DoctorModel, auth: FirebaseAuth) {
        //create a database only with the doctors.
        val firebaseDoctors = Firebase.database.getReference("Doctors")
        firebaseDoctors!!.child(auth.uid!!).setValue(doctorModel).await()
        val firebase = Firebase.database.getReference("Users")//.child("Doctors")
        firebase!!.child(auth.uid!!).setValue(doctorModel).await()
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setDisplayName(doctorModel.doctorName)
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
        isDoctor: Boolean,
        uid: String,
        //appToken: String
    ): PatientModel {

        return PatientModel(image, name, phoneNumber, email, isDoctor, uid /*appToken*/)
    }

    private fun createDoctorModel(
        image: String,
        name: String,
        phoneNumber: String,
        email: String,
        doctorLiscence: String?,
        isDoctor: Boolean,
        uid: String,
        //appToken:String
    ): DoctorModel {
        return DoctorModel(image, name, phoneNumber, email, doctorLiscence!!, isDoctor, uid)


    }

    /**
     * ----------------->
     */
    private suspend fun uploadImage(uid: String, image: Uri): Uri {

        val firebaseStorage = Firebase.storage
        val storageReference = firebaseStorage.reference
        //execute the task, wich is upload an imagge to firebaseStorage
        val task = storageReference.child(uid + PROFILE_PATH).putFile(image)
            .await() //awais is a suspend function

        return task.storage.downloadUrl.await()
    }

    /** --GET PLACES-- using google Api. */
    fun getPlaces(url: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val response = googleMapApi.getNearByPlaces(url)
        Log.d("TAG", "getPlaces:  $response ")

        if (response.body()?.googlePlaceModelList?.size!! > 0) {
            Log.d(
                "TAG",
                "getPlaces:  Success called ${response.body()?.googlePlaceModelList?.size}"
            )
            emit(State.succes(response.body()!!))
        } else {
            Log.d("TAG", "getPlaces:  failed called")
            emit(State.failed(response.body()!!.error!!))
        }

    }.catch {
        emit(State.failed(it.message.toString()))
    }.flowOn(Dispatchers.IO)


    /**
     * Method to get the places id saved in firebase from a previous session of the current user.
     * so we need its id.
     */
    suspend fun getUserLocationId(): ArrayList<String> {
        val userPlaces = ArrayList<String>()
        val auth = Firebase.auth
        val database =
            Firebase.database.getReference("Users").child(auth.uid!!).child("savedLocations")
        val data = database.get().await()
        //if we get data...
        if (data.exists()) {
            for (ds in data.children) {
                val placeId = ds.getValue(String::class.java)
                userPlaces.add(placeId!!)
            }

        }

        return userPlaces
    }

    /** -- REMOVE PLACE -- */
    fun removePlace(userSavedLocationId: ArrayList<String>, googlePlaceId: String) =
        flow<State<Any>> {
            emit(State.loading(true))
            val auth = Firebase.auth
            val database =
                Firebase.database.getReference("Users").child(auth.uid!!).child("savedLocations")
            //set the database with the remaining data not been removed.
            database.setValue(userSavedLocationId).await()

            val placesDatabase =
                Firebase.database.getReference("Places").child(auth.uid!!).child(googlePlaceId)
                    .removeValue()
            emit(State.succes("Remove Successfully"))

        }.catch {
            emit(State.failed(it.message!!))
        }.flowOn(Dispatchers.IO)

    fun addUserPlace(
        googlePlaceModel: GooglePlaceModel,
        userSavedLocationId: java.util.ArrayList<String>
    ) = flow<State<Any>> {
        emit(State.loading(true))

        val auth = Firebase.auth

        //get reference to Saved Location from current user
        val userDatabase =
            Firebase.database.getReference("Users").child(auth.uid!!).child("savedLocations")

        val database =
            Firebase.database.getReference("Places").child(auth.uid!!)
                .child(googlePlaceModel.placeId!!).get().await()
        //if the database Places does not exists, create the model and add it into firebase.
        if (!database.exists()) {
//            val savePlaceModel = SavedPlaceModel(
//                googlePlaceModel.name!!, googlePlaceModel.vicinity!!,
//                googlePlaceModel.placeId, googlePlaceModel.userRatingsTotal!!,
//                googlePlaceModel.rating!!, googlePlaceModel.geometry?.location?.lat!!,
//                googlePlaceModel.geometry.location.lng!!, googlePlaceModel.icon!!,
//                googlePlaceModel.vicinity!!
//            )
            addPlace(googlePlaceModel, auth)
        }

        //add the place id to the userSavedLocaitonId places
        userSavedLocationId.add(googlePlaceModel.placeId)
        //add the place id to the user Saved Places.
        userDatabase.setValue(userSavedLocationId).await()
        //create a flow from firebase to app
        emit(State.succes(googlePlaceModel))


    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    /**
     * method to add a place in the Places Database.
     */
    private suspend fun addPlace(googlePlaceModel: GooglePlaceModel, auth: FirebaseAuth) {
        val databse = Firebase.database.getReference("Places").child(auth.uid!!)
        //create that child from the database Places
        databse.child(googlePlaceModel.placeId!!).setValue(googlePlaceModel).await()

    }

    /** -- GET DIRECTIONS -- */

    fun getDirection(url: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))
        val response = googleMapApi.getDirection(url)
        if (response.body()?.directionRouteModels?.size!! > 0) {
            emit(State.succes(response.body()!!))
        } else {
            emit(State.failed(response.body()?.error!!))
        }
    }.catch {
        if (it.message.isNullOrEmpty()) {
            emit(State.failed("No route found"))
        } else {
            emit(State.failed(it.message.toString()))
        }

    }.flowOn(Dispatchers.IO)


    /** --AVAILABLE DOCTORS-- */

    suspend fun getAvailableDoctors(): ArrayList<DoctorModel> {
        val availableDoctList = ArrayList<DoctorModel>()

        val database = Firebase.database.reference.child("Doctors")
        val data = database.get().await()
        if (data.exists()) {
            for (d in data.children) {
                var doctorModel: DoctorModel = d.getValue(DoctorModel::class.java)!!
                availableDoctList.add(doctorModel)
            }
        }
        return availableDoctList
    }


//        Firebase.database.getReference().child("Doctors")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    availableDoctList.clear()
//                    for (childsnapshot: DataSnapshot in snapshot.children) {
//                        //create the model. if dont create not working...tonto
//                        var doctorModel: DoctorModel =
//                            childsnapshot.getValue(DoctorModel::class.java)!!
//                        availableDoctList.add(doctorModel)
//
//                    }
//                    flag = true
//
//
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    flag = false
//                    mError = error.message
//
//                }
//            })
//        if ( flag!!) {
//            emit(State.succes(availableDoctList))
//        } else emit(State.failed(mError!!))
//    }.catch {
//        emit(State.failed(it.message.toString()!!))
//    }.flowOn(Dispatchers.IO)

    /** -- CREATE PENDING APPOINTMENTS -- */

    fun createPendingDoctorPatientAppointment(
        doctorModel: DoctorModel,
        cancerList: MutableList<CancerDataEntity>, description: String, date: String, time: String
    ): Flow<State<Any>> =
        flow<State<Any>> {

            emit(State.loading(true))
            val auth = FirebaseAuth.getInstance()

            val cancerDataFirebaseModelList = ArrayList<CancerDataFirebaseModel>()
            for (c in cancerList) {
                val bitmap = c.cancerImg!!
                val bytesArrayOutputStream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytesArrayOutputStream)
                val data: ByteArray = bytesArrayOutputStream.toByteArray()
                cancerDataFirebaseModelList.add(
                    CancerDataFirebaseModel(
                        uploadCancerImg(
                            auth.uid!!,
                            data
                        ).toString(), c.date, c.outcome
                    )
                )
            }

            val map: MutableMap<String, Any> = HashMap()
            map["appointmentDate"] = date
            map["appointmentTime"] = time
            map["doctorImage"] = doctorModel.image.toString()
            map["doctorName"] = doctorModel.doctorName.toString()
            map["appointmentDescription"] = description
            map["appointmentStatus"] = "Pending acceptance by Dr.${doctorModel.doctorName}"
            map["doctorId"] = doctorModel.doctorLiscence!!
            map["doctorFirebaseId"] = doctorModel.firebaseId!!
            map["patientFirebaseId"] = auth.uid!!

            var doctorAppointmentKey =
                FirebaseDatabase.getInstance().reference.child("PendingDoctorAppointments")
                    .child(doctorModel.doctorLiscence.toString()).push().key
            map["doctorAppointmentKey"] = doctorAppointmentKey.toString()

            var patientAppointmentKey =
                FirebaseDatabase.getInstance().reference.child("PendingPatientAppointments")
                    .child(auth.uid!!).push().key
            map["patientAppointmentKey"] = patientAppointmentKey.toString()

            /**
             * ------------->
             */
            Firebase.database.reference.child("PendingPatientAppointments").child(auth.uid!!)
                .child(patientAppointmentKey.toString()).setValue(map)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val cancerL = ArrayList<CancerDataFirebaseModel>()
                        val cancerDataMap: MutableMap<String, Any> =
                            HashMap()
                        val generalBranchMap: MutableMap<String, Any> = HashMap()

                        for (c in cancerDataFirebaseModelList) {
                            cancerL.add(c)
                        }
                        cancerDataMap["cancerDataList"] = cancerL
                        Firebase.database.reference.child("PendingDoctorAppointments")
                            /*.child(doctorModel.doctorLiscence.toString())*/
                            .child(doctorModel.firebaseId!!)
                            .child(doctorAppointmentKey.toString())
                            .setValue(cancerDataMap)

                        generalBranchMap["description"] = description
                        generalBranchMap["date"] = date
                        generalBranchMap["time"] = time
                        generalBranchMap["appointmentStatus"] = "Pending to accept appointment"
                        generalBranchMap["patientId"] = auth.uid!!
                        generalBranchMap["doctorAppointmentKey"] = doctorAppointmentKey.toString()
                        generalBranchMap["patientAppointmentKey"] = patientAppointmentKey.toString()
                        generalBranchMap["doctorLiscence"] = doctorModel.doctorLiscence!!
                        generalBranchMap["doctorFirebaseId"] = doctorModel.firebaseId!!
                        Firebase.database.reference.child("PendingDoctorAppointments")
                            .child(doctorModel.firebaseId!!)
                            /*.child(doctorModel.doctorLiscence.toString())*/
                            .child(doctorAppointmentKey.toString()).updateChildren(generalBranchMap)

                        getPatientModel(doctorModel, doctorAppointmentKey!!)

                        getDoctorModel(doctorModel, doctorAppointmentKey)


                    }

                }.addOnFailureListener {

                }

            emit(State.succes("Appointment has been requested to Dr.${doctorModel.doctorName} on $date at $time"))

        }.catch { emit(State.failed(it.message!!)) }.flowOn(Dispatchers.IO)

    private fun getDoctorModel(doctorModel: DoctorModel, doctorAppointmentKey: String) {

        val doctorModelMap: MutableMap<String, Any> = HashMap()
        doctorModelMap["doctor"] = doctorModel.isDoctor!!
        doctorModelMap["doctorLiscence"] = doctorModel.doctorLiscence!!
        doctorModelMap["email"] = doctorModel.email!!
        doctorModelMap["image"] = doctorModel.image!!
        doctorModelMap["doctorName"] = doctorModel.doctorName!!
        doctorModelMap["phoneNumber"] = doctorModel.phoneNumber!!
        Firebase.database.reference.child("PendingDoctorAppointments")
            .child(doctorModel.firebaseId!!)
            /*.child(doctorModel.doctorLiscence!!)*/.child(doctorAppointmentKey)
            .child("doctorModel")
            .updateChildren(doctorModelMap).addOnSuccessListener {

            }.addOnFailureListener {

            }
    }

    private suspend fun uploadCancerImg(uid: String, image: ByteArray): Uri {
        val firebaseStorage = Firebase.storage
        val storageReference = firebaseStorage.reference


        val task = storageReference.child(uid + CANCER_PATH + System.currentTimeMillis())
            .putBytes(image).await()

        return task.storage.downloadUrl.await()

    }

    private fun getPatientModel(doctorModel: DoctorModel, doctorAppointmentKey: String) {
        val auth = FirebaseAuth.getInstance()
        val database = Firebase.database.getReference("Users").child(auth.uid!!)
        //event listener to listen for the data of an given database instance at a specific moment
        database.addValueEventListener(object : ValueEventListener {
            //used to read the database static instance
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    //create a patientModel from the firebase snapshoot.
                    val patientMap: MutableMap<String, Any> = HashMap()
                    val patientModel = snapshot.getValue(PatientModel::class.java)
                    patientMap["image"] = patientModel!!.image.toString()
                    patientMap["patientName"] = patientModel!!.patientName.toString()
                    patientMap["phoneNumber"] = patientModel!!.phoneNumber.toString()
                    patientMap["email"] = patientModel!!.email.toString()
                    patientMap["appToken"] = patientModel.appToken!!

                    Firebase.database.reference.child("PendingDoctorAppointments")
                        .child(doctorModel.firebaseId!!)
                        /*.child(doctorModel.doctorLiscence!!)*/
                        .child(doctorAppointmentKey.toString()).child("patientModel")
                        .updateChildren(patientMap).addOnSuccessListener {

                        }.addOnFailureListener {


                        }

                }

            }

            override fun onCancelled(error: DatabaseError) {


            }

        })


    }

    /**
     * GET PATIENT PENDING APOINTMENTS
     */

    suspend fun getPendingPatientAppointments(appointmentType: String): ArrayList<PendingPatientAppointmentModel> {
        val pendingPatientAppointsList = ArrayList<PendingPatientAppointmentModel>()

        val auth = Firebase.auth
        val database =
            Firebase.database.reference.child(appointmentType).child(auth.uid!!)
        val data = database.get().await()
        if (data.exists()) {
            for (d in data.children) {
                var pendingPatientAppointmentModel: PendingPatientAppointmentModel =
                    d.getValue(PendingPatientAppointmentModel::class.java)!!
                pendingPatientAppointsList.add(pendingPatientAppointmentModel)
            }
        }
        return pendingPatientAppointsList
    }

    /**
     * GET DOCTOR REQUESTED APPOINTMENTS
     */

    // TODO: 16/12/21 THIS CAN BE REFACTORES WITH THE ACCEPTED APPOINTMENS
    suspend fun getRequestedDoctorAppointments(): ArrayList<PendingDoctorAppointmentModel> {
        val pendingDoctorAppointmentsList = ArrayList<PendingDoctorAppointmentModel>()

        val auth = FirebaseAuth.getInstance()

        var database = Firebase.database.reference.child("PendingDoctorAppointments")
            .child(auth.uid!!)
        val data = database!!.get().await()
        if (data.exists()) {
            for (d in data.children) {
                val pendingDoctorAppointmentModel: PendingDoctorAppointmentModel =
                    d.getValue(PendingDoctorAppointmentModel::class.java)!!
                Log.d("pendingDoctorAppointmentModel", pendingDoctorAppointmentModel.toString())
                pendingDoctorAppointmentsList.add(pendingDoctorAppointmentModel)
            }
        }

        return pendingDoctorAppointmentsList


    }


    /**
     * DELETE PATIENT PENDING APOINTMENT
     */

    fun deletePendingPattientDoctorAppointment(
        doctorId: String,
        doctorAppointmentKey: String,
        patientAppointmentKey: String,
        uid: String,
        doctorFirebaseId: String,
        doctorAppointmentType: String,
        patientAppointmentType: String
    ): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

//        var flag = false
//        val auth = FirebaseAuth.getInstance()
        // TODO: 16/12/21 here we have to delete using the uid doctor
        Firebase.database.reference.child(doctorAppointmentType).child(doctorFirebaseId)
            .child(doctorAppointmentKey).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.database.reference.child(patientAppointmentType)
                        .child(uid).child(patientAppointmentKey).removeValue()
                        .addOnCompleteListener {
                            //flag = task.isSuccessful


                        }.addOnFailureListener {

                        }

                }

            }.addOnFailureListener {

            }

        emit(State.succes("The appointment has been canceled!"))


    }.catch { emit(State.failed(it.message!!)) }.flowOn(Dispatchers.IO)

    fun saveDoctorNotes(
        doctorNotesList: java.util.ArrayList<DoctorNoteModel>,
        patientId: String,
        patientAppointmentKey: String
    ): Flow<State<Any>> =
        flow<State<Any>> {
            emit(State.loading(true))


            val auth = Firebase.auth
            val doctorNotesMap: MutableMap<String, Any> = HashMap()
            var doctorLicence: String = ""
            val database = Firebase.database.getReference("Users").child(auth.uid!!)
            database.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val doctorModel = snapshot.getValue(DoctorModel::class.java)
                        //doctorLicence = doctorModel?.doctorLiscence!!
                        Log.d("onDataChange", doctorLicence.toString())
                        doctorNotesMap["doctorNotesList"] = doctorNotesList
                        Firebase.database.getReference("DoctorNotes")
                            .child(doctorModel!!.firebaseId!!)
                            .child(patientId).child(patientAppointmentKey).setValue(doctorNotesMap)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                    Log.d("onCancelled", error.message.toString())
                }
            })

            // TODO: 15/12/21 to get the context we have to pass it from the view. but for now no

            emit(State.succes("All notes have been saved successfully!!"))

        }.catch { emit(State.failed(it.message!!)) }.flowOn(Dispatchers.IO)

    /**
     * ------------------------------------------------------------------------------
     */
    fun saveDoctorPatientAcceptedAppointment(
        pendingAppointmentDoctorModel: PendingDoctorAppointmentModel,
        acceptedAppointmentMessage: String,
        //appointmentAccepted: Boolean,
        //lastDoctorAppointmentKeyPosition: Int
    ): Flow<State<Any>> =
        flow<State<Any>> {
            emit(State.loading(true))

            Firebase.database.reference.child("PendingDoctorAppointments")
                .child(pendingAppointmentDoctorModel.doctorFirebaseId!!)
                .child(pendingAppointmentDoctorModel.doctorAppointmentKey!!).removeValue()
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Firebase.database.reference.child("PendingPatientAppointments")
                            .child(pendingAppointmentDoctorModel.patientId!!)
                            .child(pendingAppointmentDoctorModel.patientAppointmentKey!!)
                            .removeValue()
                            .addOnCompleteListener {

                                val acceptedPatientAppointmentMap: MutableMap<String, Any> =
                                    HashMap()

                                acceptedPatientAppointmentMap["appointmentDate"] =
                                    pendingAppointmentDoctorModel.date!!
                                acceptedPatientAppointmentMap["appointmentTime"] =
                                    pendingAppointmentDoctorModel.time!!
                                acceptedPatientAppointmentMap["doctorImage"] =
                                    pendingAppointmentDoctorModel.doctorModel!!.image.toString()
                                acceptedPatientAppointmentMap["doctorName"] =
                                    pendingAppointmentDoctorModel.doctorModel.doctorName.toString()
                                acceptedPatientAppointmentMap["appointmentDescription"] =
                                    pendingAppointmentDoctorModel.description!!
                                acceptedPatientAppointmentMap["appointmentStatus"] =
                                    acceptedAppointmentMessage
                                acceptedPatientAppointmentMap["doctorId"] =
                                    pendingAppointmentDoctorModel.doctorModel.doctorLiscence!!
                                acceptedPatientAppointmentMap["doctorAppointmentKey"] =
                                    pendingAppointmentDoctorModel.doctorAppointmentKey!!
                                acceptedPatientAppointmentMap["doctorFirebaseId"] =
                                    pendingAppointmentDoctorModel.doctorFirebaseId!!
                                acceptedPatientAppointmentMap["patientAppointmentKey"] =
                                    pendingAppointmentDoctorModel.patientAppointmentKey!!


                                Firebase.database.reference.child("CancelledConfirmedPatientAppointments")
                                    .child(pendingAppointmentDoctorModel.patientId)
                                    .child(pendingAppointmentDoctorModel.patientAppointmentKey)
                                    .updateChildren(acceptedPatientAppointmentMap)
                                    .addOnCompleteListener { task ->
                                        if (task.isSuccessful && acceptedAppointmentMessage.contains(
                                                "accepted"
                                            )
                                        ) {
                                            val cancerDataMap: MutableMap<String, Any> = HashMap()
                                            if (pendingAppointmentDoctorModel.cancerDataList != null) {
                                                cancerDataMap["cancerDataList"] =
                                                    pendingAppointmentDoctorModel.cancerDataList!!
                                            }

                                            Firebase.database.reference.child("CancelledConfirmedDoctorAppointments")
                                                .child(pendingAppointmentDoctorModel.doctorFirebaseId!!)
                                                .child(pendingAppointmentDoctorModel.doctorAppointmentKey)
                                                .updateChildren(cancerDataMap)
                                                .addOnCompleteListener {

                                                }.addOnFailureListener {

                                                }

                                            val patientModelMap: MutableMap<String, Any> = HashMap()
                                            patientModelMap["patientModel"] =
                                                pendingAppointmentDoctorModel.patientModel!!

                                            Firebase.database.reference.child("CancelledConfirmedDoctorAppointments")
                                                .child(pendingAppointmentDoctorModel.doctorFirebaseId!!)
                                                .child(pendingAppointmentDoctorModel.doctorAppointmentKey)
                                                .updateChildren(patientModelMap)
                                                .addOnCompleteListener {

                                                }.addOnFailureListener {

                                                }

                                            //Accepted patient appointments JSON

                                            val patientsToCall: MutableMap<String, Any> = HashMap()
                                            patientsToCall["patientName"] =
                                                pendingAppointmentDoctorModel.patientModel.patientName!!
                                            patientsToCall["phoneNumber"] =
                                                pendingAppointmentDoctorModel.patientModel.phoneNumber!!
                                            patientsToCall["email"] =
                                                pendingAppointmentDoctorModel.patientModel.email!!
                                            patientsToCall["image"] =
                                                pendingAppointmentDoctorModel.patientModel.image!!
                                            patientsToCall["appToken"] =
                                                pendingAppointmentDoctorModel.patientModel.appToken!!
                                            Firebase.database.reference.child("PatientsToCall")
                                                .child(pendingAppointmentDoctorModel.doctorFirebaseId!!)
                                                .child(pendingAppointmentDoctorModel.doctorAppointmentKey)
                                                .updateChildren(patientsToCall)
                                                .addOnCompleteListener {

                                                }.addOnFailureListener {

                                                }


                                            val doctorModelMap: MutableMap<String, Any> = HashMap()
                                            doctorModelMap["doctorModel"] =
                                                pendingAppointmentDoctorModel.doctorModel
                                            Firebase.database.reference.child("CancelledConfirmedDoctorAppointments")
                                                .child(pendingAppointmentDoctorModel.doctorFirebaseId!!)
                                                .child(pendingAppointmentDoctorModel.doctorAppointmentKey)
                                                .updateChildren(doctorModelMap)
                                                .addOnCompleteListener {

                                                }.addOnFailureListener {

                                                }


                                            val generalBranchMap: MutableMap<String, Any> =
                                                HashMap()
                                            generalBranchMap["appointmentStatus"] =
                                                "Appointment with patient ${pendingAppointmentDoctorModel.patientModel.patientName} accepted!"
                                            generalBranchMap["date"] =
                                                pendingAppointmentDoctorModel.date
                                            generalBranchMap["description"] =
                                                pendingAppointmentDoctorModel.description
                                            generalBranchMap["doctorAppointmentKey"] =
                                                pendingAppointmentDoctorModel.doctorAppointmentKey
                                            generalBranchMap["patientAppointmentKey"] =
                                                pendingAppointmentDoctorModel.patientAppointmentKey
                                            generalBranchMap["patientId"] =
                                                pendingAppointmentDoctorModel.patientId
                                            generalBranchMap["time"] =
                                                pendingAppointmentDoctorModel.time

                                            Firebase.database.reference.child("CancelledConfirmedDoctorAppointments")
                                                .child(pendingAppointmentDoctorModel.doctorFirebaseId!!)
                                                .child(pendingAppointmentDoctorModel.doctorAppointmentKey)
                                                .updateChildren(generalBranchMap)
                                                .addOnCompleteListener {

                                                }.addOnFailureListener {

                                                }
                                        }
                                    }


                            }.addOnFailureListener {

                            }

                    }

                }.addOnFailureListener {
                }.addOnFailureListener {

                }

            if (acceptedAppointmentMessage.contains("accepted")) {
                emit(
                    State.succes(
                        "The appointment " +
                                "with ${pendingAppointmentDoctorModel.patientModel!!.patientName} has been accepted. " +
                                "Check it in 'Accepted Appointments'"
                    )
                )
            } else {
                emit(
                    State.succes(
                        "The appointment " +
                                "with ${pendingAppointmentDoctorModel.patientModel!!.patientName} has been cancelled."
                    )
                )
            }


        }.catch { emit(State.failed(it.message!!)) }.flowOn(Dispatchers.IO)

    suspend fun getAcceptedDoctorAppointments(): java.util.ArrayList<AcceptedDoctorAppointmentModel> {

        val acceptedDoctorAppointmentsList = ArrayList<AcceptedDoctorAppointmentModel>()

        val auth = Firebase.auth


        val database = Firebase.database.reference.child("CancelledConfirmedDoctorAppointments")
            .child(auth.uid!!)
        val data = database.get().await()
        if (data.exists()) {
            for (d in data.children) {
                val acceptedDoctorAppointmentModel: AcceptedDoctorAppointmentModel =
                    d.getValue(AcceptedDoctorAppointmentModel::class.java)!!
                Log.d("pendingDoctorAppointmentModel", acceptedDoctorAppointmentModel.toString())
                acceptedDoctorAppointmentsList.add(acceptedDoctorAppointmentModel)
            }
        }

        return acceptedDoctorAppointmentsList
    }

    suspend fun getPatientsToCall(): ArrayList<PatientModel> {

        val patientsToCall = ArrayList<PatientModel>()
        val auth = Firebase.auth


        val database = Firebase.database.reference.child("PatientsToCall").child(auth.uid!!)
        val data = database.get().await()
        if (data.exists()) {
            for (d in data.children) {
                var patientModel: PatientModel = d.getValue(PatientModel::class.java)!!
                patientsToCall.add(patientModel)
            }
        }
        return patientsToCall

    }


//    fun sendNotificationToPatient(notification: PushNotificationModel): Flow<State<Any>> = flow<State<Any>>{
//        emit(State.loading(true))
//
//        var flag = false
//        val response = notificationApi.postNotification(notification)
//        if (response.isSuccessful){
//            flag = true
//            Log.d(TAG, "Response: ${Gson().toJson(response)}")
//
//        }
//        if (flag)  emit(State.succes("Notification sent successfully!"))
//    }.catch { emit(State.failed(it.message!!)) }.flowOn(Dispatchers.IO)


    suspend fun sendNotificationToPatient(notification: PushNotificationModel): Response<ResponseBody> {
        return notificationApi.postNotification(notification)
    }


    /**
     * RECIPES
     */

    suspend fun getRecipes(queries: Map<String, String>): Response<FoodRecipeResponse> {
        return foodRecipeApi.getRecipes(queries)
    }


    /**
     * SETTINGS FRAGMENT
     */
    fun updateImage(image: Uri): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val auth = Firebase.auth
        val path = uploadImage(auth.uid!!, image).toString()
        val database = Firebase.database.getReference("Users").child(auth.uid!!)
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            .setPhotoUri(Uri.parse(path))
            .build()

        auth.currentUser?.updateProfile(profileChangeRequest)?.await()
        val map: MutableMap<String, Any> = HashMap()

        map["image"] = path

        database.updateChildren(map)
        emit(State.succes("Image updated"))
    }.catch { emit(State.failed(it.message!!.toString())) }.flowOn(Dispatchers.IO)

    fun updateName(name: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val auth = Firebase.auth
        //Gets a DatabaseReference for the provided path
        val database = Firebase.database.getReference("Users").child(auth.uid!!)

        /**
         * Use a class to pdate user profile information. and build it.
         */
        val profileChangeRequest = UserProfileChangeRequest.Builder()
            //Sets the updated display name
            .setDisplayName(name)
            //Returns a UserProfileChangeRequest instance with the name to update.
            .build()

        //currentUser --> Returns the currently signed-in FirebaseUser.
        //updateProfile is in charge of update the user information. And this method has to use the
        // UserProfileChangeRequest.Builder to contruct the request.
        auth.currentUser?.updateProfile(profileChangeRequest)?.await()

        /**Now we have to create a hashMap with the specific child keys that will be modifyed.
         * So because we will want to modofy the username(which is one of the tags in child firebase)
         * we will have as a key username and as a value the name entered by the user. */
        val map: MutableMap<String, Any> = HashMap()


        map["patientName"] = name
        //so now we use  that map with the key value pair to update the child.
        //Update the specific child keys to the specified values. Passing null in a map to
        // updateChildren() will remove the value at the specified location.
        database.updateChildren(map)

        emit(State.succes("Username updated"))

    }.flowOn(Dispatchers.IO)
        .catch {
            emit(State.failed(it.message!!.toString()))
        }

    fun updateEmail(email: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        //get the current authenticate user.
        val auth = Firebase.auth
        val database = Firebase.database.getReference("Users").child(auth.uid!!)
        //update the emial with the new one.
        auth.currentUser?.updateEmail(email)?.await()
        val map: MutableMap<String, Any> = HashMap()
        //??dem
        map["email"] = email
        //set the value in the database.
        database.updateChildren(map).await()

        emit(State.succes("Email updated"))
    }.flowOn(Dispatchers.IO)
        .catch {
            emit(State.failed(it.message!!.toString()))
        }

    fun updatePassword(password: String): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val auth = Firebase.auth
        auth.currentUser?.updatePassword(password)?.await()


        emit(State.succes("Email updated"))
    }.flowOn(Dispatchers.IO)
        .catch {
            emit(State.failed(it.message!!.toString()))
        }

    fun confirmEmail(authCredential: AuthCredential): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        val auth = Firebase.auth
        //Reauthenticates the user with the given credential.using the EmailAuthProvider that provide an email and pass mechanism for auth.
        auth.currentUser?.reauthenticate(authCredential)?.await()
        emit(State.succes("User authenticate"))
    }.flowOn(Dispatchers.IO)
        .catch {
            emit(State.failed(it.message!!.toString()))
        }

    suspend fun getDoctorNotes(patientAppointmentModel: PendingPatientAppointmentModel): ArrayList<CancerDoctorNote> {

        val doctorNotesList = ArrayList<CancerDoctorNote>()
        val doctorFirebaseId = patientAppointmentModel.doctorFirebaseId
        val auth = Firebase.auth
        val patientAppointmentKey = patientAppointmentModel.patientAppointmentKey

        var database =
            Firebase.database.reference.child("DoctorNotes").child(doctorFirebaseId!!)
                .child(auth.uid!!).child(patientAppointmentKey!!).child("doctorNotesList")
        val data = database!!.get().await()

        if (data.exists()) {
            for (d in data.children) {
                val cancerDoctorNote: CancerDoctorNote = d.getValue(CancerDoctorNote::class.java)!!
                doctorNotesList.add(cancerDoctorNote)
            }
        }

        return doctorNotesList


    }

    suspend fun getSavedPlaces(): java.util.ArrayList<GooglePlaceModel> {

        val googlePlaceModelList = ArrayList<GooglePlaceModel>()
        val auth = Firebase.auth

        val dataBase = Firebase.database.reference.child("Places").child(auth.uid!!)
        val data = dataBase!!.get().await()
        if (data.exists()) {
            for (d in data.children) {
                val googlePlaceModel: GooglePlaceModel = d.getValue(GooglePlaceModel::class.java)!!
                googlePlaceModelList.add(googlePlaceModel)
            }
        }

        return googlePlaceModelList


    }


    fun getDoctorAppointmentKeys(pendingAppointmentDoctorModel: PendingDoctorAppointmentModel) {

        //val doctorAppointmentKeysList = ArrayList<String>()
        var counter: Int = 0
        val auth = Firebase.auth


        // TODO: 7/1/22 VER COMO SACAR DE AQUI EL SIZE CON UN CALLBACK MIO 
        val root = FirebaseDatabase.getInstance().reference
        val users = root.child("DoctorAppointmentKeys").child(auth.uid!!)
        users.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    for (d in snapshot.children) {
                        //val doctorAppointmentKey: String = d.getValue(String::class.java)!!
                        //doctorAppointmentKeysList.add("1")
                        counter++
                    }
                    createDoctorAppointmentKeysBranch(
                        pendingAppointmentDoctorModel,
                        counter
                        //doctorAppointmentKeysList.size
                    )

                } else {
                    createDoctorAppointmentKeysBranch(
                        pendingAppointmentDoctorModel,
                        counter
                        //doctorAppointmentKeysList.size
                    )
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun createDoctorAppointmentKeysBranch(
        pendingAppointmentDoctorModel: PendingDoctorAppointmentModel,
        doctorAppointmentKeysList: Int = 0
    ) {

        val doctorAppointmentKeysMap: MutableMap<String, Any> = HashMap()
        doctorAppointmentKeysMap["$doctorAppointmentKeysList"] =
            pendingAppointmentDoctorModel.doctorAppointmentKey!!

        Firebase.database.reference.child("DoctorAppointmentKeys")
            .child(pendingAppointmentDoctorModel.doctorFirebaseId!!)
            .updateChildren(doctorAppointmentKeysMap).addOnCompleteListener {

            }.addOnFailureListener {

            }
    }

    suspend fun deletePatientCalled(position: Int): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        var doctorAppointmentKeysList = ArrayList<String>()
        var requiredDoctorAppointmentKey: String? = null
        var generalDoctorAppointmentKey: String? = null
        var deleteDoctorAppointmentKey: Boolean = false
        val map: MutableMap<String, Any> = HashMap()

        val auth = Firebase.auth
        val database = Firebase.database.reference.child("DoctorAppointmentKeys").child(auth.uid!!)
        val data = database.get().await()
        if (data.exists()) {
            for (d in data.children) {
                if (d.key == position.toString()) {
                    requiredDoctorAppointmentKey = d.getValue(String::class.java)
                }
                generalDoctorAppointmentKey = d.getValue(String::class.java)
                doctorAppointmentKeysList.add(generalDoctorAppointmentKey!!)
            }
        }
        Firebase.database.reference.child("PatientsToCall").child(auth.uid!!)
            .child(requiredDoctorAppointmentKey!!).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    doctorAppointmentKeysList.removeAt(position)
                    for ((index, value) in doctorAppointmentKeysList.withIndex()) {
                        map[index.toString()] = value
                    }
                    Firebase.database.reference.child("DoctorAppointmentKeys").child(auth.uid!!)
                        .setValue(map).addOnCompleteListener {
                        Firebase.database.reference.child("CancelledConfirmedDoctorAppointments")
                            .child(auth.uid!!)
                            .child(requiredDoctorAppointmentKey)
                            .removeValue()
                            .addOnSuccessListener {

                            }.addOnFailureListener { }
                    }.addOnFailureListener { }
                }
            }
        emit(State.succes("The call has been deleted"))


    }.catch { State.failed<Any>(it.message!!.toString()) }.flowOn(Dispatchers.IO)


}






