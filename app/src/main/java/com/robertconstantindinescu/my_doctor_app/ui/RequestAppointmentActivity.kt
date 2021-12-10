package com.robertconstantindinescu.my_doctor_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.navArgs
import coil.load
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityRequestAppointmentBinding
import kotlinx.android.synthetic.main.activity_request_appointment.*

class RequestAppointmentActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityRequestAppointmentBinding
    private val args by navArgs<RequestAppointmentActivityArgs>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityRequestAppointmentBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        with(mBinding){
            img_doctor.load(args.doctorModel.image)
            txtView_doctorName.text = args.doctorModel.name.toString()
            txtView_doctorEmail.text = args.doctorModel.email.toString()
            txtView_doctorLiscence.text = args.doctorModel.doctorLiscence.toString()
        }

    }
}