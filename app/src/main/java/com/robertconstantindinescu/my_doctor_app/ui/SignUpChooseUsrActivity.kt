package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivitySignUpChooseUsrBinding

class SignUpChooseUsrActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivitySignUpChooseUsrBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivitySignUpChooseUsrBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        with(mBinding) {

            btnDoctorSignUp.setOnClickListener {
                val intent = Intent(this@SignUpChooseUsrActivity,
                    DoctorSignUpActivity::class.java)
                intent.putExtra("isDoctor", true)
                startActivity(intent)
            }
            btnPatientSignUp.setOnClickListener {
                val intent = Intent(this@SignUpChooseUsrActivity,
                    PatientSignUpActivity::class.java)
                intent.putExtra("isDoctor", false)
                startActivity(intent)
            }


        }
    }
}