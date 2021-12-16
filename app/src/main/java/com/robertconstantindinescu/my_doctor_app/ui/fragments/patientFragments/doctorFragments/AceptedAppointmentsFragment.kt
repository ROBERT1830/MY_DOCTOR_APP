package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AcceptedAppointmentsAdapter
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AppointmentsRequestedAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAceptedAppointmentsBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.AcceptedDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AceptedAppointmentsFragment : Fragment() {

    private lateinit var mBinding: FragmentAceptedAppointmentsBinding
    private val mAdapter by lazy { AcceptedAppointmentsAdapter() }
    private lateinit var acceptedDoctorAppointmentsList: ArrayList<AcceptedDoctorAppointmentModel>
    private lateinit var loadingDialog: LoadingDialog
    private val requestAppointmentsViewModel: RequestAppointmentViewModel by viewModels()

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
        mBinding = FragmentAceptedAppointmentsBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(requireActivity())
        acceptedDoctorAppointmentsList = ArrayList()

        setUpRecyclerView()

        lifecycleScope.launchWhenStarted {
            getAcceptedAppointments()
        }



        return mBinding.root
    }


    private fun setUpRecyclerView() {

        mBinding.recyclerViewAcceptedAppointments.apply {
            adapter = mAdapter
            setHasFixedSize(false)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }
    private suspend fun getAcceptedAppointments() {


        loadingDialog.startLoading()
        acceptedDoctorAppointmentsList =
            requestAppointmentsViewModel.getAcceptedDoctorAppointments()

        if (!acceptedDoctorAppointmentsList.isNullOrEmpty()) {
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(acceptedDoctorAppointmentsList)

        } else {
            mAdapter.setUpAdapter(acceptedDoctorAppointmentsList)

            loadingDialog.stopLoading()
            Toast.makeText(
                requireContext(),
                "No appointments requested at the moment",
                Toast.LENGTH_SHORT
            ).show()

        }


    }




}