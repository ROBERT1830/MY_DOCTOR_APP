package com.robertconstantindinescu.my_doctor_app.ui.fragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.PendingPatientAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPendingPatientAppointmentBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PatientAppointmentInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PendingPatientAppointmentFragment : Fragment(), PatientAppointmentInterface {

    private lateinit var mBinding: FragmentPendingPatientAppointmentBinding
    private val requestAppointmentViewModel: RequestAppointmentViewModel by viewModels()

    // TODO: 11/12/21 do the adapter.
    private val mAdapter by lazy { PendingPatientAppointmentAdapter(this) }
    private lateinit var pendingPatientAppontmentList: ArrayList<PendingPatientAppointmentModel>
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

        mBinding = FragmentPendingPatientAppointmentBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(requireActivity())
        pendingPatientAppontmentList = ArrayList()
        setUpRecyclerView()



        lifecycleScope.launchWhenStarted {
            getPatientPendingAppointments()
        }


        return mBinding.root


    }

    private suspend fun getPatientPendingAppointments() {
        loadingDialog.startLoading()
        pendingPatientAppontmentList =
            requestAppointmentViewModel.getPatientPendingAppointments()

        if (!pendingPatientAppontmentList.isNullOrEmpty()) {
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(pendingPatientAppontmentList)
        } else {
            loadingDialog.startLoading()
            Toast.makeText(
                requireContext(),
                "No appointments requested at the moment",
                Toast.LENGTH_SHORT
            ).show()
        }


    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewPendingAppointments.apply {
            adapter = mAdapter
            setHasFixedSize(true)
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
        val alertDialog = AlertDialog.Builder(requireContext()).setCancelable(false)
            .setMessage(resources.getString(R.string.cancel_specific_appointment))
            .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                pendingPatientAppontmentList.remove(patientAppointmentModel)
                lifecycleScope.launchWhenStarted {
                    requestAppointmentViewModel.deletePendingPattientDoctorAppointment(
                        patientAppointmentModel.doctorId!!,
                        patientAppointmentModel.doctorAppointmentKey!!,
                        patientAppointmentModel.patientAppointmentKey!!,
                    ).collect {
                        when (it) {
                            is State.Loading -> {
                                if (it.flag == true) loadingDialog.startLoading()
                            }
                            is State.Succes -> {
                                loadingDialog.stopLoading()
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
            }).setNegativeButton("No", null).show();


    }


}