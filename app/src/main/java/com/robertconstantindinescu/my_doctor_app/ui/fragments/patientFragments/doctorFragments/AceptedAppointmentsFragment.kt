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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AcceptedAppointmentsAdapter
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AppointmentsRequestedAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAceptedAppointmentsBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.AcceptedDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.CheckInternet
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
        mBinding = FragmentAceptedAppointmentsBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(requireActivity())
        acceptedDoctorAppointmentsList = ArrayList()
        swipeRefreshLayout = mBinding.swipeRefreshRecycler

        setUpRecyclerView()


        if(CheckInternet.hasInternetConnection(requireContext())){
            lifecycleScope.launchWhenStarted {
                getAcceptedAppointments()
            }


        }else{
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }



        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
            if(CheckInternet.hasInternetConnection(requireContext())){
                lifecycleScope.launchWhenStarted {
                    getAcceptedAppointments()
                }

            }else{
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }

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