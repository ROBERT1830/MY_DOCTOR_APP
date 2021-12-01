package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityLoginBinding
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog

class LoginActivity : AppCompatActivity() {
    //binding
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        //get bolean from intetn
        val bundle :Bundle ?=intent.extras
        if (bundle!=null){
            val isDoctor = bundle.getBoolean("isDoctor") // 1

            Log.i("isDoctor", isDoctor.toString())

        }


        loadingDialog = LoadingDialog(this)

        with(mBinding) {
            //llamas a una función con que tiene como parametro una lambda.
            //if you put this, the context will be the mBinding (not what we want)
            btnSignUp.setOnClickListener {
                startActivity(Intent(this@LoginActivity,
                    SignUpChooseUsrActivity::class.java))

            }
        }
    }
}