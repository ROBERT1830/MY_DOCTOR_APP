package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.ui.loginSignUp.LoginActivity

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)


        Handler(Looper.getMainLooper()).postDelayed({
            checkUserAccessLevel()
        }, 3000)
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
}