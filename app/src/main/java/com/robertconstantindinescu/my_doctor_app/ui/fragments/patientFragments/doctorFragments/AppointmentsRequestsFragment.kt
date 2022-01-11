package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments

import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.net.Uri
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
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AppointmentsRequestedAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.DoctorAppointmentKeysCallBack
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.FROM_SAVE_DOCTOR_NOTES
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.doctorCancelChoice
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
    //private lateinit var doctorAppointmentKeysList: ArrayList<String>
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var fromSavedDoctorNote = false
    var flag = false

    //position of cancel option
    var cancelOption = 0

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
        //requestedDoctorAppointmentsList = ArrayList()
        //doctorAppointmentKeysList = ArrayList()
        swipeRefreshLayout = mBinding.swipeRefreshRecycler


        val data = arguments
        if (data != null) {
            fromSavedDoctorNote = data.getBoolean(FROM_SAVE_DOCTOR_NOTES)
        }



        setUpRecyclerView()






        return mBinding.root
    }

    override fun onResume() {
        super.onResume()
        lifecycleScope.launchWhenStarted {
            getRequestedAppointments()
        }
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

        //
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
            mAdapter.setUpAdapter(requestedDoctorAppointmentsList)
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

    override fun onAcceptDoctorPendingAppointmentClick(
        pendingAppointmentDoctorModel: PendingDoctorAppointmentModel,
        position: Int
    ) {

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

            val acceptedAppointmentMessage =
                resources.getString(R.string.accept_appointment_title) +
                        "${pendingAppointmentDoctorModel.doctorModel!!.doctorName}"

            val alertDialog = AlertDialog.Builder(requireContext())

            alertDialog.setTitle("Information")
                .setMessage(this.resources.getString(R.string.accept_appointment) /*+ " ${pendingAppointmentDoctorModel.patientModel!!.patientName}"*/)

            alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { _, _ ->

//                var lastDoctorAppointmentKeyPosition: Int? = null
//                var doctorAppointmentKeysList = ArrayList<String>()


                sendEmail(true, pendingAppointmentDoctorModel)
                cancelAcceptAppointment(pendingAppointmentDoctorModel, acceptedAppointmentMessage, true)


            })
            alertDialog.setNegativeButton("Cancel", null)
            alertDialog.setCancelable(false).show()
            fromSavedDoctorNote = false
        }

    }

    override fun onCancelDoctorPendingAppointmentClick(pendingAppointmentDoctorModel: PendingDoctorAppointmentModel) {

        var cancelAppointmentMessage: String = ""
        val alertDialog = AlertDialog.Builder(requireContext())
        alertDialog.setTitle(resources.getString(R.string.reasson_appointment_cancelation))
        alertDialog.setSingleChoiceItems(
            doctorCancelChoice,
            0
        ) { dialogInterface: DialogInterface, position: Int ->
            cancelOption = position
            cancelAppointmentMessage =
                " The appointment with ${pendingAppointmentDoctorModel.doctorModel!!.doctorName} " +
                        "has been cancelled with the next reason: " +
                        "" + doctorCancelChoice[position]
            Toast.makeText(requireContext(), "You selected option $position", Toast.LENGTH_LONG)
                .show()
        }

        alertDialog.setPositiveButton("Accept", DialogInterface.OnClickListener { _, _ ->

//            val emailDoctor = pendingAppointmentDoctorModel.doctorModel!!.email
//            val passwordSender = "rjjrjpenaxdnavac"


            sendEmail(false, pendingAppointmentDoctorModel)
            cancelAcceptAppointment(pendingAppointmentDoctorModel, cancelAppointmentMessage, false)
            //sendEmailToPatient(emailDoctor!!, passwordSender, pendingAppointmentDoctorModel, false)


        })

        alertDialog.setNegativeButton("Cancel", null)
        alertDialog.setCancelable(false).show()


    }

    private fun sendEmail(
        acceptAppointment: Boolean,
        pendingAppointmentDoctorModel: PendingDoctorAppointmentModel
    ) {

        var subject = ""
        var body = ""

        if (acceptAppointment) {
            subject =
                "Accepted Appointment with ${pendingAppointmentDoctorModel.doctorModel!!.doctorName}"
            body =
                "On ${pendingAppointmentDoctorModel.date} at ${pendingAppointmentDoctorModel.time} " +
                        "you have an appointment with Dr.${pendingAppointmentDoctorModel.doctorModel.doctorName}. " +
                        "Please make sure you will assist"
        } else {
            subject =
                "Appointment with ${pendingAppointmentDoctorModel.doctorModel!!.doctorName} Cancelled"
            body = "The main reason is: ${doctorCancelChoice[cancelOption]} "
        }

        val address =
            pendingAppointmentDoctorModel.patientModel!!.email!!.split(",".toRegex()).toTypedArray()
        val intent = Intent(Intent.ACTION_SENDTO).apply {
            //with this line of code only email apps will handle this intent
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, address)
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, body)
        }


        //check if the device has an app to handle the intent request.
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)

        } else Toast.makeText(requireContext(), "Required App is not installed", Toast.LENGTH_SHORT)
            .show()

    }



    private fun cancelAcceptAppointment(
        pendingAppointmentDoctorModel: PendingDoctorAppointmentModel,
        AppointmentMessage: String,
        appointmentAccepted:Boolean
    ) {
        // TODO: 8/1/22 ver esto porwue me sale null  
        //var lastDoctorAppointmentKeyPosition: Int? = null
        //var doctorAppointmentKeysList = ArrayList<String>()
        if (appointmentAccepted){
            lifecycleScope.launchWhenStarted {
//                requestAppointmentsViewModel.getDoctorAppointmentKeys(object : DoctorAppointmentKeysCallBack{
//                    override fun onGetAppointmentKeysCallBack(doctorAppointmentKeysList: ArrayList<String>) {
//                        lastDoctorAppointmentKeyPosition = doctorAppointmentKeysList.size
//                        Log.d("lastDoctorAppointmentKeyPosition", lastDoctorAppointmentKeyPosition.toString())
//                    }
//                })
                requestAppointmentsViewModel.getDoctorAppointmentKeys(pendingAppointmentDoctorModel)

            }
        }
        //requestedDoctorAppointmentsList.remove(pendingAppointmentDoctorModel)
        lifecycleScope.launchWhenStarted {
            requestAppointmentsViewModel.saveCancelDoctorPatientAcceptedAppointment(
                pendingAppointmentDoctorModel,
                AppointmentMessage
            ).collect {
                when (it) {
                    is State.Loading -> {
                        if (it.flag == true) {
                            loadingDialog.startLoading()
                        }
                    }
                    is State.Succes -> {
                        loadingDialog.stopLoading()
                        //mAdapter.setUpAdapter(requestedDoctorAppointmentsList)
                        //mAdapter.notifyDataSetChanged()
                        //mAdapter.setUpAdapter(requestedDoctorAppointmentsList)

                        //mAdapter.delete(pendingAppointmentDoctorModel)
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
    }


}