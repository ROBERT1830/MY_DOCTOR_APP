package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments

import android.content.Intent
import android.os.Bundle
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
import com.robertconstantindinescu.my_doctor_app.ui.DoctorVideoCallActivity
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.ROOM_CODE
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.PatientToCallViewModel
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.http.POST
import kotlin.streams.asSequence
import android.R
import android.app.AlertDialog
import android.content.DialogInterface
import android.opengl.Visibility
import android.util.Log
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.robertconstantindinescu.my_doctor_app.utils.CheckInternet
import kotlinx.android.synthetic.main.fragment_contact_to_patient_row.view.*
import kotlinx.coroutines.flow.collect


@AndroidEntryPoint
class ContactToPatientFragment : Fragment(), ContactPatientInterface {


    private lateinit var mBinding: FragmentContactToPatientBinding
    private val patientToCallViewModel: PatientToCallViewModel by viewModels<PatientToCallViewModel>()
    private val mAdapter by lazy { ContactToPatientAdapter(this) }

    private lateinit var patientsToCallList: ArrayList<PatientModel>
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

    private var mPosition: Int? = null
    private var videoCallClicked = false
    private var viewItem: View? = null

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
        swipeRefreshLayout = mBinding.swipeRefreshRecycler
        setUpRecyclerView()

        if(CheckInternet.hasInternetConnection(requireContext())){
            lifecycleScope.launchWhenStarted {
                getPatientsToCall()

            }

        }else{
            Toast.makeText(
                requireContext(),
                resources.getString(com.robertconstantindinescu.my_doctor_app.R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }

        return mBinding.root
    }

    private suspend fun getPatientsToCall() {
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


    override fun onResume() {
        super.onResume()
        if (videoCallClicked) {
            changeDeleteCallButtonVisibility()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
            if(CheckInternet.hasInternetConnection(requireContext())){
                lifecycleScope.launchWhenStarted {
                    getPatientsToCall()

                }

            }else{
                Toast.makeText(
                    requireContext(),
                    resources.getString(com.robertconstantindinescu.my_doctor_app.R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }


    }


    private fun changeDeleteCallButtonVisibility() {
        Log.d("changeDeleteCallButtonVisibility", "--->changeDeleteCallButtonVisibility called")

        if (videoCallClicked) {
            viewItem = mBinding.recyclerViewPatientsToCall.layoutManager
                ?.findViewByPosition(mPosition!!)!!

            viewItem!!.imgView_deleteCall.visibility = View.VISIBLE
            videoCallClicked = false
            mPosition = 0
        } else {
            if (viewItem != null) {
                viewItem!!.imgView_deleteCall.visibility = View.GONE
                videoCallClicked = false
                mPosition = 0
            }
        }
    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewPatientsToCall.apply {
            adapter = mAdapter
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    override fun onVideoCallClick(patientModel: PatientModel, position: Int) {

        // TODO: 7/1/22 we can pass he position from here and the use that position, or store it ina  global variable and then use it to acces the imageview from recycler child.

        if(CheckInternet.hasInternetConnection(requireContext())){
            this.mPosition = position
            videoCallClicked = true
            //generate the random room string
            val source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
            val roomCode = java.util.Random().ints(10, 0, source.length)
                .asSequence()
                .map(source::get)
                .joinToString("")

            PushNotificationModel(
                NotificationDataModel(
                    "Hi ${patientModel.patientName}",
                    "You have a video call appointment with the next code $roomCode"
                ), patientModel.appToken!!
            ).also {
                sendNotificationToPatient(it)
            }

            val intent = Intent(requireContext(), DoctorVideoCallActivity::class.java)
            intent.putExtra(ROOM_CODE, roomCode)
            startActivity(intent)
        }else{
            Toast.makeText(
                requireContext(),
                resources.getString(com.robertconstantindinescu.my_doctor_app.R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }



    }

    override fun onDeleteCallClick(position: Int) {

        if(CheckInternet.hasInternetConnection(requireContext())){
            val alertDialog = AlertDialog.Builder(requireContext())
            alertDialog.setTitle(resources.getString(com.robertconstantindinescu.my_doctor_app.R.string.delete_call))
            alertDialog.setPositiveButton("YES", DialogInterface.OnClickListener { _, _ ->

//            viewItem = mBinding.recyclerViewPatientsToCall.layoutManager
//                ?.findViewByPosition(mPosition!!)!!
                viewItem!!.imgView_deleteCall.visibility = View.GONE
                patientsToCallList.removeAt(position)


                lifecycleScope.launchWhenCreated {
                    patientToCallViewModel.deletePatientCalled(position).collect {
                        when(it){
                            is State.Loading ->{
                                if (it.flag == true){
                                    loadingDialog.startLoading()
                                }
                            }
                            is State.Succes ->{
                                loadingDialog.stopLoading()
                                mAdapter.setUpAdapter(patientsToCallList)
                                mAdapter.notifyDataSetChanged()
                                Snackbar.make(mBinding.root, it.data.toString(), Snackbar.LENGTH_SHORT).show()
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
            }).setNegativeButton("CANCEL", DialogInterface.OnClickListener { _, _ ->
                null
            }).show()
        }else{
            Toast.makeText(
                requireContext(),
                resources.getString(com.robertconstantindinescu.my_doctor_app.R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
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
                is State.Failed -> {
                    loadingDialog.stopLoading()
                    Snackbar.make(
                        mBinding.root,
                        response.error.toString(),
                        Snackbar.LENGTH_LONG
                    ).show()

                }
            }
        })

    }


}