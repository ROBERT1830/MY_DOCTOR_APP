package com.robertconstantindinescu.my_doctor_app.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.PendingPatientAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPendingPatientAppointmentBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PendingPatientAppointmentFragment : Fragment() {

    private lateinit var mBinding: FragmentPendingPatientAppointmentBinding
    private val requestAppointmentViewModel: RequestAppointmentViewModel by viewModels()

    // TODO: 11/12/21 do the adapter.
    private val mAdapter by lazy { PendingPatientAppointmentAdapter() }
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
            loadingDialog.startLoading()
            pendingPatientAppontmentList =
                requestAppointmentViewModel.getPatientPendingAppointments()
            if (!pendingPatientAppontmentList.isNullOrEmpty()) {
                loadingDialog.stopLoading()
                mAdapter.setUpAdapter(pendingPatientAppontmentList)
            } else {
                loadingDialog.stopLoading()
                Toast.makeText(
                    requireContext(),
                    "No pending appointments at the moemnt",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }


        return mBinding.root


    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewPendingAppointments.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

    }


}