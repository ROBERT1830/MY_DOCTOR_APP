package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.ui.loginSignUp.LoginActivity
import com.robertconstantindinescu.my_doctor_app.utils.NetworkListener
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesQueryUtilsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SplashActivity : AppCompatActivity() {

    private lateinit var recipesQueryUtilsViewModel: RecipesQueryUtilsViewModel
    private  var networkListener: NetworkListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        recipesQueryUtilsViewModel =
            ViewModelProvider(this).get(RecipesQueryUtilsViewModel::class.java)

        recipesQueryUtilsViewModel.readBackOnline.observe(this, Observer {
            recipesQueryUtilsViewModel.backOnline = it
        })

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener!!.checkNetworkAvailability(this@SplashActivity).collect { status ->

                Log.d("NetworkListener", status.toString())
                recipesQueryUtilsViewModel.networkStatus = status
                recipesQueryUtilsViewModel.showNetworkStatus()
                Handler(Looper.getMainLooper()).postDelayed({
                    if(recipesQueryUtilsViewModel.networkStatus){
                        checkUserAccessLevel()
                    }else{
                        recipesQueryUtilsViewModel.showNetworkStatus()
                    }

                }, 2000)
            }
        }


    }


    private fun checkUserAccessLevel() {

        val uid = FirebaseAuth.getInstance().currentUser?.uid
        if(uid != null){
            val db = FirebaseDatabase.getInstance().reference
            val uidRef = db.child("Users").child(uid!!)
            val valueEventListener = object : ValueEventListener {
                //retrieve the data from a field using ValueEventListener.
                //with onDataChange, we get a snapshot from the database wich is an instance of the database
                override fun onDataChange(snapshot: DataSnapshot) {
                    //get the value from the doctor field.
                    val doctor = snapshot.child("doctor").value as Boolean
                    //check for its value and start corresponding activity.
                    if (doctor) {
                        startActivity(
                            Intent(
                                this@SplashActivity,
                                DoctorActivity::class.java
                            )
                        )

                    } else {
                        startActivity(
                            Intent(
                                this@SplashActivity,
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
        }else{
            startActivity(Intent(this, LoginActivity::class.java))
        }

    }
    override fun onStop() {
        super.onStop()
        finish()
        networkListener = null
    }
}