package com.robertconstantindinescu.my_doctor_app.ui.appointmetsActitvities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.DetailAcceptedPatientAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDetailAcceptedPatientAppointmentBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDoctorNote
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.RequestAppointmentViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailAcceptedPatientAppointmentActivity : AppCompatActivity() {

    private lateinit var mBinding: ActivityDetailAcceptedPatientAppointmentBinding
    private val args by navArgs<DetailAcceptedPatientAppointmentActivityArgs>()
    private val requestAppointmentsViewModel: RequestAppointmentViewModel by viewModels()

    // TODO: 6/1/22 declare adapter
    private val mAdapter by lazy { DetailAcceptedPatientAppointmentAdapter() }

    private lateinit var cancerDoctorNotesList: ArrayList<CancerDoctorNote>
    private lateinit var loadingDialog: LoadingDialog


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDetailAcceptedPatientAppointmentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        loadingDialog = LoadingDialog(this)
        cancerDoctorNotesList = ArrayList()

        setUpRecyclerView()

        lifecycleScope.launchWhenStarted {
            getCancerDoctorNotes(args.patientAppointmentModel)
        }


    }

    private fun setUpRecyclerView() {
        mBinding.recyclerViewPatientAcceptedAppointmentDetail.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(
                this@DetailAcceptedPatientAppointmentActivity,
                LinearLayoutManager.VERTICAL,
                false
            )
        }

    }

    private suspend fun getCancerDoctorNotes(patientAppointmentModel: PendingPatientAppointmentModel) {

        loadingDialog.startLoading()
        cancerDoctorNotesList = requestAppointmentsViewModel.getDoctorNotes(patientAppointmentModel)

        if (!cancerDoctorNotesList.isNullOrEmpty()){
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(cancerDoctorNotesList)
        }else{
            loadingDialog.stopLoading()
            Toast.makeText(
                this,
                "No notes at the moment",
                Toast.LENGTH_SHORT
            ).show()
        }

    }



















}