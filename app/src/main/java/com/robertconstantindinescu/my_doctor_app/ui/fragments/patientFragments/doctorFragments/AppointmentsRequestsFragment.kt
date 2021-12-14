package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments

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
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AppointmentsRequestedAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsBinding
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsRowBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PENDING_DOCTOR_APPOINTMENT_MODEL
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.lang.Exception

@AndroidEntryPoint
class AppointmentsRequestsFragment : Fragment(), PendingDoctorAppointmentRequestsInterface {


    private lateinit var mBinding: FragmentAppointmentsRequestsBinding
    private val requestAppointmentsViewModel: RequestAppointmentViewModel by viewModels()

    private val mAdapter by lazy { AppointmentsRequestedAdapter(this) }
    private lateinit var requestedDoctorAppointmentsList: ArrayList<PendingDoctorAppointmentModel>
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

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
        mBinding = FragmentAppointmentsRequestsBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(requireActivity())
        requestedDoctorAppointmentsList = ArrayList()
        swipeRefreshLayout = mBinding.swipeRefreshRecycler


        setUpRecyclerView()

        lifecycleScope.launchWhenStarted {
            getRequestedAppointments()
        }




        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {

            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
            lifecycleScope.launchWhenStarted {

                getRequestedAppointments()

            }


        }

    }

    private suspend fun getRequestedAppointments() {

        loadingDialog.startLoading()
        requestedDoctorAppointmentsList =
            requestAppointmentsViewModel.getRequestedDoctorAppointments()

        if (!requestedDoctorAppointmentsList.isNullOrEmpty()) {
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(requestedDoctorAppointmentsList)
        } else {
            loadingDialog.stopLoading()
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
            setHasFixedSize(false)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

    }

    override fun onDoctorPendingAppointmentRequestClick(pendingAppointmentDoctorModel: PendingDoctorAppointmentModel) {


        try {
            val action =
                AppointmentsRequestsFragmentDirections.actionRequestedAppointmentsFragmentToPatientAppointmentDetailsActivity(
                    pendingAppointmentDoctorModel
                )
            findNavController().navigate(action)
        } catch (e: Exception) {
            Log.d("onDoctorPendingAppointmentRequestClick", e.toString())
        }


    }


}