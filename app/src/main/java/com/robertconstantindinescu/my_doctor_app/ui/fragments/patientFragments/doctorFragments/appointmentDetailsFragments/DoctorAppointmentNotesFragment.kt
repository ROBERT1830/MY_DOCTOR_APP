package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments.appointmentDetailsFragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.internal.FallbackServiceBroker
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.DoctorAppointmentNotesAdapter
import com.robertconstantindinescu.my_doctor_app.bindingAdapters.DoctorNotesBinding.Companion.cancerImagesMap
import com.robertconstantindinescu.my_doctor_app.bindingAdapters.DoctorNotesBinding.Companion.doctorNotesMap
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentDoctorAppointmentNotesBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.DoctorNoteModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PENDING_DOCTOR_APPOINTMENT_MODEL
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_doctor_appointment_notes.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class DoctorAppointmentNotes : Fragment() {

    private lateinit var mBinding: FragmentDoctorAppointmentNotesBinding
    private lateinit var pendingDoctorAppointmentModel: PendingDoctorAppointmentModel


    private val requestAppointmentViewModel: RequestAppointmentViewModel by viewModels()

    private var doctorNotesList = arrayListOf<String>()

    private val mAdapter by lazy { DoctorAppointmentNotesAdapter() }
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pendingDoctorAppointmentModel =
                it.getParcelable<PendingDoctorAppointmentModel>(PENDING_DOCTOR_APPOINTMENT_MODEL)!!
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentDoctorAppointmentNotesBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(requireActivity())

        setUpRecyclerView()
        showCancerOutcome()

        mBinding.btnSaveNotes.setOnClickListener {
            getAllDoctorNotes()

        }




        return mBinding.root

    }

    private fun getAllDoctorNotes() {
        var emptyNote: Boolean = false
        val doctorNotesList = ArrayList<DoctorNoteModel>()
        val cancerImagesMap: HashMap<Int, String> = cancerImagesMap
        val doctorNotes: HashMap<Int, String> = doctorNotesMap
        for (i in 0 until cancerImagesMap.size) {

            var uri: String = cancerImagesMap.getValue(i)
            var note: String = doctorNotes.getValue(i)
            if (note.contains(this.resources.getString(R.string.default_text_doctor_note))) emptyNote =
                true
            doctorNotesList.add(DoctorNoteModel(uri, note))
        }

        Log.d("doctorNoteModel", doctorNotesList.toString())


        saveDoctorNotes(emptyNote, doctorNotesList)


    }

    private fun saveDoctorNotes(emptyNote: Boolean, doctorNotesList: ArrayList<DoctorNoteModel>) {
        if (emptyNote) {
            val alertDialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Information")
                .setMessage(this.resources.getString(R.string.save_doctor_note_warning))
            alertDialog.setPositiveButton("Agree", DialogInterface.OnClickListener { _, _ ->
                    saveNotesIntoFirebase(doctorNotesList)

                })
            alertDialog.setNegativeButton("Cancel", null)
            alertDialog.setCancelable(false).show()
        }else{
            saveNotesIntoFirebase(doctorNotesList)
        }
    }

    private fun saveNotesIntoFirebase(doctorNotesList: ArrayList<DoctorNoteModel>) {



        lifecycleScope.launchWhenStarted {

            requestAppointmentViewModel.saveDoctorNotes(
                doctorNotesList,
                pendingDoctorAppointmentModel.patientId!!,
                pendingDoctorAppointmentModel.date!!,
                pendingDoctorAppointmentModel.time!!,
            ).collect{
                when(it){
                    is State.Loading -> {
                        if (it.flag == true){
                            loadingDialog.startLoading()
                        }
                    }
                    is State.Succes -> {
                        loadingDialog.stopLoading()
                        // TODO: 11/12/21 here we can make a toast
                        Snackbar.make(
                            mBinding.root,
                            it.data.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()
                        // TODO: 11/12/21 here we can use the scroll event in the snackbar so that it finishes go to the fargment by using onbackpress. we have used that when delete a location.


                    }
                    is State.Failed -> {
                        loadingDialog.stopLoading()
                        Snackbar.make(
                            mBinding.root,
                            it.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }






    private fun showCancerOutcome() {

        if (!pendingDoctorAppointmentModel.cancerDataList.isNullOrEmpty()) {
            mAdapter.setUpAdapter(pendingDoctorAppointmentModel.cancerDataList!!)
        } else {
            // TODO: 15/12/21 Should be changed by the images like recipes.
            Toast.makeText(
                requireContext(),
                "No records in this appointment",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewCancerNotes.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }


}