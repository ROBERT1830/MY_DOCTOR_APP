package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.PendingPatientAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPatientAcceptedAppointmentsBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PatientAcceptedAppointmentInterface
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingPatientAppointmentInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.lang.Exception

@AndroidEntryPoint
class PatientAcceptedAppointmentsFragment : Fragment(), PendingPatientAppointmentInterface,
    PatientAcceptedAppointmentInterface {

    private lateinit var mBinding: FragmentPatientAcceptedAppointmentsBinding
    private val requestAppointmentViewModel: RequestAppointmentViewModel by viewModels()
    private val mAdapter by lazy { PendingPatientAppointmentAdapter(this, this) }
    private lateinit var acceptedPatientAppointmentList: ArrayList<PendingPatientAppointmentModel>
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        mBinding = FragmentPatientAcceptedAppointmentsBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(requireActivity())
        acceptedPatientAppointmentList = ArrayList()
        setUpRecyclerView()

        lifecycleScope.launchWhenStarted {
            getPatientAcceptedAppointments()
        }


        return mBinding.root
    }

    private suspend fun getPatientAcceptedAppointments() {
        loadingDialog.startLoading()
        acceptedPatientAppointmentList =
            requestAppointmentViewModel.getPatientPendingAppointments("CancelledConfirmedPatientAppointments")

        if (!acceptedPatientAppointmentList.isNullOrEmpty()) {
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(acceptedPatientAppointmentList)
        } else {
            loadingDialog.stopLoading()
            Toast.makeText(
                requireContext(),
                "No accepted appointments at the moment",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewAcceptedAppointments.apply {
            adapter = mAdapter
            setHasFixedSize(false)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

    }

    override fun onCancelButtonClick(
        patientAppointmentModel: PendingPatientAppointmentModel
//        doctorId: String,
//        doctorAppointmentKey: String,
//        patientAppointmentKey: String
    ) {
        Log.d("AcceptedPatientAppointmentModel", patientAppointmentModel.toString())
        val confirmedDoctorAppointment = "CancelledConfirmedDoctorAppointments"
        val confirmedPatientAppointment = "CancelledConfirmedPatientAppointments"
         AlertDialog.Builder(requireContext()).setCancelable(false)
            .setMessage(resources.getString(R.string.cancel_specific_appointment))
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                acceptedPatientAppointmentList.remove(patientAppointmentModel)
                lifecycleScope.launchWhenStarted {
                    val auth = Firebase.auth
                    requestAppointmentViewModel.deletePendingPattientDoctorAppointment(
                        patientAppointmentModel.doctorId!!,
                        patientAppointmentModel.doctorAppointmentKey!!,
                        patientAppointmentModel.patientAppointmentKey!!,
                        auth.uid!!,
                        patientAppointmentModel.doctorFirebaseId!!,
                        confirmedDoctorAppointment,
                        confirmedPatientAppointment


                    ).collect {
                        when (it) {
                            is State.Loading -> {
                                if (it.flag == true) loadingDialog.startLoading()
                            }
                            is State.Succes -> {
                                loadingDialog.stopLoading()
                                mAdapter.setUpAdapter(acceptedPatientAppointmentList)
                                mAdapter.notifyDataSetChanged()
                                Snackbar.make(
                                    mBinding.root,
                                    it.data.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()

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
            }).setNegativeButton("No", null).setCancelable(false).show();


    }

    override fun onAcceptedAppointmentClick(patientAppointmentModel: PendingPatientAppointmentModel) {

        try{
            val action = PatientAcceptedAppointmentsFragmentDirections.actionBtnMyAppointmentsToDetailAcceptedPatientAppointment(patientAppointmentModel)
            findNavController().navigate(action)


        }catch (e:Exception){
            Log.d("onAcceptedAppointmentClick", e.toString())
        }


    }


}