package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.doctorFragments.appointmentDetailsFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.PatientAppointmentDetailsAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPatientAppointmentDetailsBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.PENDING_DOCTOR_APPOINTMENT_MODEL
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog


class PatientAppointmentDetailsFragment : Fragment() {

    private lateinit var mBinding: FragmentPatientAppointmentDetailsBinding

    private lateinit var pendingDoctorAppointmentModel: PendingDoctorAppointmentModel
    private val mAdapter by lazy { PatientAppointmentDetailsAdapter() }
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            pendingDoctorAppointmentModel =
                it.getParcelable<PendingDoctorAppointmentModel>(PENDING_DOCTOR_APPOINTMENT_MODEL)!!

            Log.d("myBundle", pendingDoctorAppointmentModel.toString())
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentPatientAppointmentDetailsBinding.inflate(layoutInflater)

        loadingDialog = LoadingDialog(requireActivity())

        with(mBinding) {
            patientImage.load(pendingDoctorAppointmentModel.patientModel!!.image)
            txtViewPatientName.text = pendingDoctorAppointmentModel.patientModel!!.name
            txtViewPatientEmail.text = pendingDoctorAppointmentModel.patientModel!!.email
            txtViewPatientPhone.text = pendingDoctorAppointmentModel.patientModel!!.phoneNumber
            txtViewPatientDescription.text = pendingDoctorAppointmentModel.description

        }

        setUpRecyclerView()
        showPatientCancerData()


        return mBinding.root
    }

    private fun showPatientCancerData() {

        if (!pendingDoctorAppointmentModel.cancerDataList.isNullOrEmpty()) {
            mAdapter.setUpAdapter(pendingDoctorAppointmentModel.cancerDataList!!)
        } else {
            Toast.makeText(
                requireContext(),
                "No patient records in this appointment",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewCancerData.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }


}