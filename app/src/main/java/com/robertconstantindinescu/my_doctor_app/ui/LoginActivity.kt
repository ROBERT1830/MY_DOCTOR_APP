package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityLoginBinding
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.flow.collect
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener
import kotlinx.coroutines.tasks.await
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase

import com.google.firebase.auth.FirebaseUser

import com.google.firebase.database.DatabaseReference
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


@AndroidEntryPoint
class LoginActivity : AppCompatActivity() {
    //binding
    private lateinit var mBinding: ActivityLoginBinding
    private lateinit var loadingDialog: LoadingDialog
    private val loginViewModel: LoginViewModel by viewModels()
    private lateinit var email: String
    private lateinit var password: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val isDoctor = false

        //get bolean from inteNT
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val isDoctor = bundle.getBoolean("isDoctor") // 1

            Log.i("isDoctor", isDoctor.toString())

        }


        loadingDialog = LoadingDialog(this)

        with(mBinding) {
            //llamas a una función con que tiene como parametro una lambda.
            //if you put this, the context will be the mBinding (not what we want)
            btnSignUp.setOnClickListener {
                startActivity(
                    Intent(
                        this@LoginActivity,
                        SignUpChooseUsrActivity::class.java
                    )
                )

            }
        }

        //Login
        mBinding.btnLogin.setOnClickListener {
            if (areFieldsReady()) {
                lifecycleScope.launchWhenStarted {

                    loginViewModel.login(email, password).collect { loginResult ->
                        when (loginResult) {
                            is State.Loading -> {
                                if (loginResult.flag == true) loadingDialog.startLoading()
                            }
                            is State.Succes -> {

                                loadingDialog.stopLoading()
                                Snackbar.make(
                                    mBinding.root,
                                    loginResult.data.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()

                                lifecycleScope.launch(Dispatchers.IO) {
                                    checkUserAccesLevel()

                                }

                            }
                            is State.Failed -> {
                                loadingDialog.stopLoading()
                                Snackbar.make(
                                    mBinding.root,
                                    loginResult.error,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }

        }
    }

    private fun checkUserAccesLevel() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val db = FirebaseDatabase.getInstance().reference
        val uidRef = db.child("Users").child(uid!!)
        val valueEventListener = object : ValueEventListener {
            //retrieve the data from a field using ValueEventListener.
            //with onDataChange, we get a snapshot from the database wich is an instance of the database
            override fun onDataChange(snapshot: DataSnapshot) {
                //get the value from the doctor field.
                val doctor = snapshot.child("doctor").getValue() as Boolean
                //check for its value and start corresponding activity.
                if (doctor) {
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            DoctorActivity::class.java
                        )
                    )
                    finish()
                } else {
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            MainActivity::class.java
                        )
                    )
                    finish()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Log.d(
                    "TAG",
                    databaseError.getMessage()
                ) //Don't ignore potential errors!
            }
        }
        uidRef.addListenerForSingleValueEvent(valueEventListener)
    }

    override fun onStart() {
        super.onStart()
        lifecycleScope.launch(Dispatchers.IO) {
            checkUserAccesLevel()
        }
    }

    private fun areFieldsReady(): Boolean {
        with(mBinding) {
            email = edtEmail.text.trim().toString()
            password = edtPassword.text.trim().toString()
        }

        var view: View? = null
        var isEmty: Boolean = false

        when {
            email.isEmpty() -> {
                with(mBinding) {
                    edtEmail.error = resources.getString(R.string.field_required)
                    view = edtEmail
                    isEmty = true

                }
            }
            password.isEmpty() -> {
                with(mBinding) {
                    edtPassword.error = resources.getString(R.string.field_required)
                    view = edtPassword
                    isEmty = true
                }
            }
        }
        return if (isEmty) {
            view?.requestFocus()
            false
        } else {
            true
        }


    }
}