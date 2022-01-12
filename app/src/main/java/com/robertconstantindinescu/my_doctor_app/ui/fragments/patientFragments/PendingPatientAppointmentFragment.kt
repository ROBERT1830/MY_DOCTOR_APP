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
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.PendingPatientAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPendingPatientAppointmentBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PatientAcceptedAppointmentInterface
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingPatientAppointmentInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.CheckInternet
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.NetworkListener
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesQueryUtilsViewModel
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class PendingPatientAppointmentFragment : Fragment(), PendingPatientAppointmentInterface,
    PatientAcceptedAppointmentInterface {

    private lateinit var mBinding: FragmentPendingPatientAppointmentBinding
    private val requestAppointmentViewModel: RequestAppointmentViewModel by viewModels()


    private val mAdapter by lazy { PendingPatientAppointmentAdapter(this, this) }
    private lateinit var pendingPatientAppontmentList: ArrayList<PendingPatientAppointmentModel>
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

        mBinding = FragmentPendingPatientAppointmentBinding.inflate(layoutInflater)
        loadingDialog = LoadingDialog(requireActivity())
        pendingPatientAppontmentList = ArrayList()
        swipeRefreshLayout = mBinding.swipeRefreshRecycler
        setUpRecyclerView()

        if (CheckInternet.hasInternetConnection(requireContext())){
            lifecycleScope.launchWhenStarted {
                getPatientPendingAppointments()
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

                    getPatientPendingAppointments()
                    Log.d("requestedDoctorAppointmentsList", pendingPatientAppontmentList.toString())
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



    private suspend fun getPatientPendingAppointments() {
        loadingDialog.startLoading()
        pendingPatientAppontmentList =
            requestAppointmentViewModel.getPatientPendingAppointments("PendingPatientAppointments")

        if (!pendingPatientAppontmentList.isNullOrEmpty()) {
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(pendingPatientAppontmentList)
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
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }

    }

    override fun onCancelButtonClick(
        patientAppointmentModel: PendingPatientAppointmentModel

    ) {
        if (CheckInternet.hasInternetConnection(requireContext())) {
            Log.d("PendingPatientAppointmentModel", patientAppointmentModel.toString())
            val pendingDoctorAppointment = "PendingDoctorAppointments"
            val pendingPatientAppointment = "PendingPatientAppointments"
            val alertDialog = AlertDialog.Builder(requireContext()).setCancelable(false)
                .setMessage(resources.getString(R.string.cancel_specific_appointment))
                .setPositiveButton("Yes", DialogInterface.OnClickListener { _, _ ->
                    pendingPatientAppontmentList.remove(patientAppointmentModel)
                    lifecycleScope.launchWhenStarted {
                        val auth = Firebase.auth
                        requestAppointmentViewModel.deletePendingPattientDoctorAppointment(
                            patientAppointmentModel.doctorId!!,
                            patientAppointmentModel.doctorAppointmentKey!!,
                            patientAppointmentModel.patientAppointmentKey!!,
                            auth.uid!!,
                            patientAppointmentModel.doctorFirebaseId!!,
                            pendingDoctorAppointment,
                            pendingPatientAppointment
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
        } else {
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    override fun onAcceptedAppointmentClick(patientAppointmentModel: PendingPatientAppointmentModel) {
        return
    }


}