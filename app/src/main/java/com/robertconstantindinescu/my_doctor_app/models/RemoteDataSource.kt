package com.robertconstantindinescu.my_doctor_app.models

import android.app.Application
import android.net.Uri
import android.util.Log
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDataFirebaseModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.GoogleMapApi
import com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex.UVResponse
import com.robertconstantindinescu.my_doctor_app.models.onlineData.network.UvRadiationApi
import com.robertconstantindinescu.my_doctor_app.models.placesModel.SavedPlaceModel
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PROFILE_PATH
import com.robertconstantindinescu.my_doctor_app.utils.State
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.tasks.await
import retrofit2.Response
import javax.inject.Inject
import kotlin.concurrent.timerTask


class RemoteDataSource @Inject constructor(
    private val uvRadiationApi: UvRadiationApi,
    private val googleMapApi: GoogleMapApi,
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


//    public val isNetworkAvailable = MutableStateFlow(false)
//    fun  checkForUserAcces(): MutableStateFlow<Boolean> {
//
//        val uid = FirebaseAuth.getInstance().currentUser?.uid
//        val db = FirebaseDatabase.getInstance().reference
//        val uidRef = db.child("Users").child(uid!!)
//        val valueEventListener = object : ValueEventListener {
//            override fun  onDataChange(snapshot: DataSnapshot) {
//                val doctor = snapshot.child("doctor").getValue() as Boolean
//                if(doctor) {
//                    isNetworkAvailable.value = true
//                } else {
//                    isNetworkAvailable.value = true
//                }
//            }
//
//            override fun onCancelled(databaseError: DatabaseError) {
//                Log.d("TAG", databaseError.getMessage()) //Don't ignore potential errors!
//            }
//        }
//        uidRef.addListenerForSingleValueEvent(valueEventListener)
//        return  isNetworkAvailable
//    }


//    private fun checkUserAccesLevel(uid: String?) {
//        val firebase = Firebase.database.getReference("Users").child(uid!!)
//        firebase.get().addOnSuccessListener {
//            if(it.value. != null){
//
//
//            }
//        }
//
//
//    }


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
            if (isDoctor) {
                doctorModel =
                    createDoctorModel(path, name, phoneNumber, email, doctorLiscence, isDoctor)
                createDoctor(doctorModel, auth)
            } else {
                patientModel = createPatientModel(path, name, phoneNumber, email, isDoctor)
                createPatient(patientModel, auth)
            }
            emit(State.succes("Email verification sent"))


        }
    }.catch {
        emit(State.failed(it.message!!))
    }.flowOn(Dispatchers.IO)

    private suspend fun createPatient(patientModel: PatientModel, auth: FirebaseAuth) {
        val firebase = Firebase.database.getReference("Users")//.child("Patients")
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
        //create a database only with the doctors.
        val firebaseDoctors = Firebase.database.getReference("Doctors")
        firebaseDoctors!!.child(auth.uid!!).setValue(doctorModel).await()
        val firebase = Firebase.database.getReference("Users")//.child("Doctors")
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
    ): PatientModel {

        return PatientModel(image, name, phoneNumber, email, isDoctor)
    }

    private fun createDoctorModel(
        image: String,
        name: String,
        phoneNumber: String,
        email: String,
        doctorLiscence: String?,
        isDoctor: Boolean
    ): DoctorModel {
        return DoctorModel(image, name, phoneNumber, email, doctorLiscence!!, isDoctor)


    }

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
            Firebase.database.getReference("Users").child(auth.uid!!).child("Saved Locations")
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
    fun removePlace(userSavedLocationId: ArrayList<String>) = flow<State<Any>> {
        emit(State.loading(true))
        val auth = Firebase.auth
        val database =
            Firebase.database.getReference("Users").child(auth.uid!!).child("Saved Locations")
        //set the database with the remaining data not been removed.
        database.setValue(userSavedLocationId).await()
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
            Firebase.database.getReference("Users").child(auth.uid!!).child("Saved Locations")

        val database =
            Firebase.database.getReference("Places").child(googlePlaceModel.placeId!!).get().await()
        //if the database Places does not exists, create the model and add it into firebase.
        if (!database.exists()) {
            val savePlaceModel = SavedPlaceModel(
                googlePlaceModel.name!!, googlePlaceModel.vicinity!!,
                googlePlaceModel.placeId, googlePlaceModel.userRatingsTotal!!,
                googlePlaceModel.rating!!, googlePlaceModel.geometry?.location?.lat!!,
                googlePlaceModel.geometry.location.lng!!
            )
            addPlace(savePlaceModel)
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
    private suspend fun addPlace(savePlaceModel: SavedPlaceModel) {
        val databse = Firebase.database.getReference("Places")
        //create that child from the database Places
        databse.child(savePlaceModel.placeId).setValue(savePlaceModel).await()

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
        //var flag: Boolean? = null
        //var mError: String? = null
        //emit(State.loading(true))

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

            val auth = FirebaseAuth.getInstance()
            emit(State.loading(true))

            val map: MutableMap<String, Any> = HashMap()
            map["appointmentDate"] = date
            map["appointmentTime"] = time
            map["doctorImage"] = doctorModel.image.toString()
            map["doctorName"] = doctorModel.name.toString()
            map["appointmentDescription"] = description
            map["appointmentStatus"] = "Pending acceptance by Dr.${doctorModel.name}"
            map["doctorId"] = doctorModel.doctorLiscence!!

            var doctorAppointmentKey =
                FirebaseDatabase.getInstance().reference.child("PendingDoctorAppointments")
                    .child(doctorModel.doctorLiscence.toString()).push().key
            map["doctorAppointmentKey"] = doctorAppointmentKey.toString()

            var patientAppointmentKey =
                FirebaseDatabase.getInstance().reference.child("PendingPatientAppointments")
                    .child(auth.uid!!).push().key
            map["patientAppointmentKey"] = patientAppointmentKey.toString()

            Firebase.database.reference.child("PendingPatientAppointments").child(auth.uid!!)
                .child(patientAppointmentKey.toString()).setValue(map)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        var cancerL = ArrayList<CancerDataFirebaseModel>()
                        val cancerDataMap: MutableMap<String, Any> =
                            HashMap()
                        val generalBranchMap: MutableMap<String, Any> = HashMap()
                        //cancerDataMap["cancerDataList"] = ArrayList<CancerDataFirebaseModel>()



                        for (c in cancerList) {

                            val cancerObject =
                                CancerDataFirebaseModel(c.cancerImg.toString(), c.date, c.outcome)

                            cancerL.add(cancerObject)

//                            cancerDataMap["cancerImg"] = c.cancerImg.toString()
//                            cancerDataMap["outcome"] = c.outcome.toString()
//                            cancerDataMap["date"] = c.date.toString()
//                            Firebase.database.reference.child("PendingDoctorAppointments")
//                                .child(doctorModel.doctorLiscence.toString())
//                                .child(doctorAppointmentKey.toString()).child("cancerDataList")
//                                .child(c.id.toString()).setValue(cancerDataMap)
                        }
                        cancerDataMap["cancerDataList"] = cancerL
                        Firebase.database.reference.child("PendingDoctorAppointments")
                            .child(doctorModel.doctorLiscence.toString())
                            .child(doctorAppointmentKey.toString())
                            .setValue(cancerDataMap)

                        generalBranchMap["description"] = description
                        generalBranchMap["date"] = date
                        generalBranchMap["time"] = time
                        generalBranchMap["appointmentStatus"] = "Pending to accept appointment"
                        Firebase.database.reference.child("PendingDoctorAppointments")
                            .child(doctorModel.doctorLiscence.toString())
                            .child(doctorAppointmentKey.toString()).updateChildren(generalBranchMap)

                        getPatientModel(doctorModel, doctorAppointmentKey!!)


                    }

                }.addOnFailureListener {

                }

            emit(State.succes("Appointment has been requested to Dr.${doctorModel.name} on $date at $time"))

        }.catch { emit(State.failed(it.message!!)) }.flowOn(Dispatchers.IO)

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
                    patientMap["name"] = patientModel!!.name.toString()
                    patientMap["phoneNumber"] = patientModel!!.phoneNumber.toString()
                    patientMap["email"] = patientModel!!.email.toString()

                    Firebase.database.reference.child("PendingDoctorAppointments")
                        .child(doctorModel.doctorLiscence!!)
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

    suspend fun getPendingPatientAppointments(): ArrayList<PendingPatientAppointmentModel> {
        val pendingPatientAppointsList = ArrayList<PendingPatientAppointmentModel>()

        val auth = FirebaseAuth.getInstance()
        val database =
            Firebase.database.reference.child("PendingPatientAppointments").child(auth.uid!!)
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

    suspend fun getRequestedDoctorAppointments(): ArrayList<PendingDoctorAppointmentModel> {
        val pendingDoctorAppointmentsList = ArrayList<PendingDoctorAppointmentModel>()

        val auth = FirebaseAuth.getInstance()
        //var doctorId = auth.uid!![2].toString()

        val database = Firebase.database.reference.child("PendingDoctorAppointments")
            .child("1234")
        val data = database.get().await()
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
    ): Flow<State<Any>> = flow<State<Any>> {
        emit(State.loading(true))

        var flag = false
        val auth = FirebaseAuth.getInstance()
        Firebase.database.reference.child("PendingDoctorAppointments").child(doctorId)
            .child(doctorAppointmentKey).removeValue().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Firebase.database.reference.child("PendingPatientAppointments")
                        .child(auth.uid!!).child(patientAppointmentKey).removeValue()
                        .addOnCompleteListener {
                            //flag = task.isSuccessful


                        }.addOnFailureListener {

                        }

                }

            }.addOnFailureListener {

            }

        emit(State.succes("The appointment has been canceled!"))


    }.catch { emit(State.failed(it.message!!)) }.flowOn(Dispatchers.IO)


}






