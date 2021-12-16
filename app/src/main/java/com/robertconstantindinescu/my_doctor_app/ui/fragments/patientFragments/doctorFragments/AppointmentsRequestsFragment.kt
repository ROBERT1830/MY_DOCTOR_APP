package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.os.BaseBundle
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
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AppointmentsRequestedAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsBinding
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsRowBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.FROM_SAVE_DOCTOR_NOTES
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PENDING_DOCTOR_APPOINTMENT_MODEL
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import java.lang.Exception

@AndroidEntryPoint
class AppointmentsRequestsFragment : Fragment(), PendingDoctorAppointmentRequestsInterface {


    private lateinit var mBinding: FragmentAppointmentsRequestsBinding
    private val requestAppointmentsViewModel: RequestAppointmentViewModel by viewModels()

    private val mAdapter by lazy { AppointmentsRequestedAdapter(this) }
    private lateinit var requestedDoctorAppointmentsList: ArrayList<PendingDoctorAppointmentModel>
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var fromSavedDoctorNote = false

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


        val data = arguments
        if (data != null) {
            fromSavedDoctorNote = data.getBoolean(FROM_SAVE_DOCTOR_NOTES)
        }



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
                Log.d("requestedDoctorAppointmentsList", requestedDoctorAppointmentsList.toString())

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
            mAdapter.setUpAdapter(requestedDoctorAppointmentsList)
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

    override fun onAcceptDoctorPendingAppointmentClick(pendingAppointmentDoctorModel: PendingDoctorAppointmentModel) {

        if (!fromSavedDoctorNote) {
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Information")
                .setMessage(this.resources.getString(R.string.accept_appointment_no_valid))
            alertDialog.setPositiveButton("GO", DialogInterface.OnClickListener { _, _ ->
                val action =
                    AppointmentsRequestsFragmentDirections.actionRequestedAppointmentsFragmentToPatientAppointmentDetailsActivity(
                        pendingAppointmentDoctorModel
                    )
                findNavController().navigate(action)
            })
            alertDialog.setNegativeButton("Cancel", null)
            alertDialog.setCancelable(false).show()
        } else {
            // TODO: 16/12/21 What we have to do is first delete the current pendincdoctorappoint. Add a listener on complete and delete the patient pending appointment. Ad listener and then create accepted doctor appointment. Litener and finally create the aacepted patientn apponitment.
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle("Information")
                .setMessage(this.resources.getString(R.string.accept_appointment) + " ${pendingAppointmentDoctorModel.patientModel!!.name}")
            alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { _, _ ->

                requestedDoctorAppointmentsList.remove(pendingAppointmentDoctorModel)

                Log.d("requestedDoctorAppointmentsList", requestedDoctorAppointmentsList.toString())
                lifecycleScope.launchWhenStarted {
                    requestAppointmentsViewModel.saveDoctorPatientAcceptedAppointment(
                        pendingAppointmentDoctorModel
                    ).collect {
                        when(it){
                            is State.Loading -> {
                                if (it.flag == true){
                                    loadingDialog.startLoading()
                                }
                            }
                            is State.Succes -> {
                                loadingDialog.stopLoading()
                                mAdapter.notifyDataSetChanged()
                                //mAdapter.setUpAdapter(requestedDoctorAppointmentsList)
                                Snackbar.make(
                                    mBinding.root,
                                    it.data.toString(),
                                    Snackbar.LENGTH_LONG
                                ).show()

                            }
                            is State.Failed -> {
                                loadingDialog.stopLoading()
                                Snackbar.make(
                                    mBinding.root,
                                    it.error.toString(),
                                    Snackbar.LENGTH_LONG
                                ).show()

                            }
                        }
                    }
                }

            })
            alertDialog.setNegativeButton("Cancel", null)
            alertDialog.setCancelable(false).show()
        }
    }


}