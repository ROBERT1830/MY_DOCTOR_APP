package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityPatientSignUpBinding

class PatientSignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityPatientSignUpBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPatientSignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        mBinding.txtLogin.setOnClickListener {
            val intent =  Intent(this, LoginActivity::class.java)
            intent.putExtra("isDoctor", false)
            startActivity(intent)
        }
        mBinding.btnSignUp.setOnClickListener {
            val intent =  Intent(this, LoginActivity::class.java)
            intent.putExtra("isDoctor", false)
            startActivity(intent)
        }
    }
}