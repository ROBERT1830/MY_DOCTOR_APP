package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.ContactToPatientAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentContactToPatientBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.ContactPatientInterface
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel
import com.robertconstantindinescu.my_doctor_app.models.notificationModels.NotificationDataModel
import com.robertconstantindinescu.my_doctor_app.models.notificationModels.PushNotificationModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.PatientToCallViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class ContactToPatientFragment : Fragment(), ContactPatientInterface {


    private lateinit var mBinding: FragmentContactToPatientBinding
    private val patientToCallViewModel: PatientToCallViewModel by viewModels<PatientToCallViewModel>()
    private val mAdapter by lazy { ContactToPatientAdapter(this) }

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

    override fun onVideoCallClick(patientModel: PatientModel) {

        //create pushNotification Object
        PushNotificationModel(
            NotificationDataModel(
                "Hi ${patientModel.patientName}",
                "You have a video call appointment with Dr..."
            ), patientModel.appToken!!
        ).also {
            sendNotificationToPatient(it)
        }


    }

    private fun sendNotificationToPatient(notification: PushNotificationModel) {


        patientToCallViewModel.sendNotificationToPatient(notification)
        patientToCallViewModel.notificationData.observe(viewLifecycleOwner, Observer { response ->
            when (response) {
                is State.Loading -> {
                    if (response.flag == true) loadingDialog.startLoading() else loadingDialog.stopLoading()
                }
                is State.Succes -> {
                    loadingDialog.stopLoading()
                    Toast.makeText(requireContext(), response.data.toString(), Toast.LENGTH_SHORT)
                        .show()

                }
                is State.Failed ->{
                    loadingDialog.stopLoading()
                    Snackbar.make(
                            mBinding.root,
                            response.error.toString(),
                            Snackbar.LENGTH_LONG
                        ).show()

                }
            }
        })


//        lifecycleScope.launchWhenStarted {
//            patientToCallViewModel.sendNotificationToPatient(notification).collect {
//                when (it) {
//                    is State.Loading -> {
//                        if (it.flag == true) loadingDialog.startLoading()
//                    }
//                    is State.Succes -> {
//                        loadingDialog.stopLoading()
//                        Log.d("succesCalled", it.data.toString())
//                        Snackbar.make(
//                            mBinding.root,
//                            it.data.toString(),
//                            Snackbar.LENGTH_LONG
//                        ).show()
//                        //start the new activity
//
//                    }
//                    is State.Failed -> {
//                        loadingDialog.stopLoading()
//                        Log.d("failedCalled", it.error.toString())
//                        Snackbar.make(
//                            mBinding.root,
//                            it.error.toString(),
//                            Snackbar.LENGTH_LONG
//                        ).show()
//
//                    }
//                }
//            }
//        }


    }


}