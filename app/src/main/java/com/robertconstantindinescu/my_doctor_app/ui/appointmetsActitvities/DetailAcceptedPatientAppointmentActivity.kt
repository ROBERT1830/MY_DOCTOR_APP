package com.robertconstantindinescu.my_doctor_app.ui.appointmetsActitvities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
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

    private var areNotesAvailable: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDetailAcceptedPatientAppointmentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        loadingDialog = LoadingDialog(this)
        cancerDoctorNotesList = ArrayList()

        setUpRecyclerView()

        mBinding.btnBack.setOnClickListener {
            finish()
        }

        lifecycleScope.launchWhenStarted {
            areNotesAvailable = getCancerDoctorNotes(args.patientAppointmentModel)
            Log.d("areNotesAvailable", areNotesAvailable.toString())
            when(areNotesAvailable){
                true -> {
                    mBinding.recyclerViewPatientAcceptedAppointmentDetail.visibility = View.VISIBLE
                    mBinding.noDataImageView.visibility = View.GONE
                    mBinding.noDataTextView.visibility = View.GONE
                }
                false -> {
                    mBinding.recyclerViewPatientAcceptedAppointmentDetail.visibility = View.GONE
                    mBinding.noDataImageView.visibility = View.VISIBLE
                    mBinding.noDataTextView.visibility = View.VISIBLE
                }
            }
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

    private suspend fun getCancerDoctorNotes(patientAppointmentModel: PendingPatientAppointmentModel): Boolean {

        loadingDialog.startLoading()
        cancerDoctorNotesList = requestAppointmentsViewModel.getDoctorNotes(patientAppointmentModel)

        if (!cancerDoctorNotesList.isNullOrEmpty()){
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(cancerDoctorNotesList)
            return true
        }else{
            loadingDialog.stopLoading()
            Toast.makeText(
                this,
                "No notes at the moment",
                Toast.LENGTH_SHORT
            ).show()
        }
        return false

    }



















}