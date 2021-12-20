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
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.ContactToPatientAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentContactToPatientBinding
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.PatientToCallViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ContactToPatientFragment : Fragment() {


    private lateinit var mBinding: FragmentContactToPatientBinding
    private val patientToCallViewModel: PatientToCallViewModel by viewModels<PatientToCallViewModel>()
    private val mAdapter by lazy { ContactToPatientAdapter() }

    private lateinit var patientsToCallList: ArrayList<PatientModel>
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
        mBinding = FragmentContactToPatientBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        patientsToCallList = ArrayList()

        setUpRecyclerView()

        lifecycleScope.launchWhenStarted {
            loadingDialog.startLoading()
            patientsToCallList = patientToCallViewModel.getPatientsToCall()
            if (!patientsToCallList.isNullOrEmpty()) {
                loadingDialog.stopLoading()
                mAdapter.setUpAdapter(patientsToCallList)
            } else {
                loadingDialog.stopLoading()
                Toast.makeText(
                    requireContext(),
                    "No available patients to call at the moment",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

        return mBinding.root
    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewPatientsToCall.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }


}