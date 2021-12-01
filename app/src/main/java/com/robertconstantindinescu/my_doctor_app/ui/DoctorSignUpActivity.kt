package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDoctorSignUpBinding
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityPatientSignUpBinding

class DoctorSignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityDoctorSignUpBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDoctorSignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.txtLogin.setOnClickListener {
            val intent =  Intent(this, LoginActivity::class.java)
            intent.putExtra("isDoctor", true)
            startActivity(intent)
        }

        mBinding.btnSignUp.setOnClickListener {
            val intent =  Intent(this, LoginActivity::class.java)
            intent.putExtra("isDoctor", true)
            startActivity(intent)
        }
    }
}