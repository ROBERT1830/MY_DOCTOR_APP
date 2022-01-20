package com.robertconstantindinescu.my_doctor_app.ui.loginSignUp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityLoginBinding
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_login.*
import kotlinx.coroutines.flow.collect
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseError

import com.google.firebase.database.DataSnapshot

import com.google.firebase.database.ValueEventListener

import com.google.firebase.database.FirebaseDatabase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.ui.DoctorActivity
import com.robertconstantindinescu.my_doctor_app.ui.PatientActivity
import com.robertconstantindinescu.my_doctor_app.utils.NetworkListener
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesQueryUtilsViewModel

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

    private lateinit var recipesQueryUtilsViewModel: RecipesQueryUtilsViewModel
    private  var networkListener: NetworkListener? = null

    private var fromSignUp:Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(mBinding.root)
        networkListener = NetworkListener()

        recipesQueryUtilsViewModel =
            ViewModelProvider(this).get(RecipesQueryUtilsViewModel::class.java)

        val intent = intent
        fromSignUp = intent.getBooleanExtra("fromSignUp", false)

        lifecycleScope.launch(Dispatchers.IO) {
            //val uid = FirebaseAuth.getInstance().currentUser?.uid
            if (FirebaseAuth.getInstance().currentUser != null) {
                //that's for prevent autologin when came from signUp
                if (!fromSignUp){
                    checkUserAccesLevel()
                }


            }


        }

        //val isDoctor = false

        //get bolean from inteNT





//        val bundle: Bundle? = intent.extras
//        if (bundle != null) {
//            val isDoctor = bundle.getBoolean("isDoctor") // 1
//
//            Log.i("isDoctor", isDoctor.toString())
//
//        }
// TODO: 11/1/22 MODULARIZAR 

        loadingDialog = LoadingDialog(this)

        recipesQueryUtilsViewModel.readBackOnline.observe(this, Observer {
            recipesQueryUtilsViewModel.backOnline = it
        })
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener!!.checkNetworkAvailability(this@LoginActivity).collect { status ->

                Log.d("NetworkListener", status.toString())
                recipesQueryUtilsViewModel.networkStatus = status
                recipesQueryUtilsViewModel.showNetworkStatus()

                if(recipesQueryUtilsViewModel.networkStatus){
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

                }else{
                    recipesQueryUtilsViewModel.showNetworkStatus()
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

                } else {
                    startActivity(
                        Intent(
                            this@LoginActivity,
                            PatientActivity::class.java
                        )
                    )

                }
                // TODO: 23/12/21 here qe can make a when and a default option that returnand do nothing.
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

    /**
     * Here we destroy the networkListener so that in the remaining app, we will handle the internet connection and
     * avoid memory leaks. If we don't do that the networklistener will be listening for internet connection each and every time.
     */
    override fun onStop() {
        super.onStop()

        networkListener = null
    }


}