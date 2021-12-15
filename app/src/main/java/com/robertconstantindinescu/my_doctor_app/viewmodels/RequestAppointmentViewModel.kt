package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.models.Repository
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDataFirebaseModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.DoctorNoteModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import dagger.hilt.android.lifecycle.HiltViewModel
import java.util.ArrayList
import javax.inject.Inject

@HiltViewModel
class RequestAppointmentViewModel @Inject constructor(
    private val repository: Repository,
    application: Application
) : AndroidViewModel(application) {

    fun createPendingDoctorPatientAppointment(
        doctorModel: DoctorModel,
        cancerList: MutableList<CancerDataEntity>, description: String, date: String, time: String
    ) =
        repository.remote.createPendingDoctorPatientAppointment(
            doctorModel,
            cancerList,
            description,
            date,
            time
        )


    suspend fun getPatientPendingAppointments() = repository.remote.getPendingPatientAppointments()

    suspend fun getRequestedDoctorAppointments() = repository.remote.getRequestedDoctorAppointments()

    fun deletePendingPattientDoctorAppointment(
        doctorId: String,
        doctorAppointmentKey: String,
        patientAppointmentKey: String,
    ) = repository.remote.deletePendingPattientDoctorAppointment(
        doctorId,
        doctorAppointmentKey,
        patientAppointmentKey,

    )

    fun saveDoctorNotes(doctorNotesList: ArrayList<DoctorNoteModel>, patientId:String, date:String, time:String) =

        repository.remote.saveDoctorNotes(doctorNotesList, patientId, date, time)



//    fun getDoctorNotes(recyclerView: RecyclerView): ArrayList<String> {
//
//        var doctorNotesList = arrayListOf<String>()
//
//        var child: View? = null
//        for (i in 0 until recyclerView.childCount){
//            child = recyclerView.getChildAt(i)
//            child.text
//        }
//
//
//        for (i in 0 until mRecycler.getChildCount()) {
//            child = mRecycler.getChildAt(i)
//            //In case you need to access ViewHolder:
//            mRecycler.getChildViewHolder(child)
//        }
//
//
//    }


}