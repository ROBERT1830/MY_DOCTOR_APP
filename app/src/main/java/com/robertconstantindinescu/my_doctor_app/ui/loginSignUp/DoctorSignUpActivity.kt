package com.robertconstantindinescu.my_doctor_app.ui.loginSignUp

import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDoctorSignUpBinding
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.LoginViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

@AndroidEntryPoint
class DoctorSignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityDoctorSignUpBinding
    private lateinit var appPermissions: AppPermissions
    private lateinit var loadingDialog: LoadingDialog

    private var image: Uri? = null
    private lateinit var name: String
    private lateinit var phoneNumber: String
    private lateinit var email: String
    private lateinit var doctorliscence: String
    private lateinit var password: String

    private val loginViewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDoctorSignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        appPermissions = AppPermissions()
        loadingDialog = LoadingDialog(this)

        mBinding.btnBack.setOnClickListener { onBackPressed() }

        mBinding.txtLogin.setOnClickListener { onBackPressed() }

        mBinding.imgPick.setOnClickListener {
            if (appPermissions.isStorageOk(this)) pickImage()
            else appPermissions.requestStoragePermission(this)
        }


        mBinding.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("isDoctor", true)
            startActivity(intent)
        }

        mBinding.btnSignUp.setOnClickListener {
            if (areFieldsReady()) {
                if (image != null) {
                    lifecycleScope.launchWhenStarted {
                        loginViewModel.signUp(
                            image!!,
                            name,
                            phoneNumber,
                            email,
                            password,
                            doctorliscence,
                            isDoctor = true
                        ).collect { result ->
                            when (result) {
                                is State.Loading -> {
                                    if (result.flag == true) loadingDialog.startLoading()
                                }
                                is State.Succes -> {
                                    loadingDialog.stopLoading()
                                    Snackbar.make(
                                        mBinding.root, result.data.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                    val intent =
                                        Intent(this@DoctorSignUpActivity, LoginActivity::class.java)
                                    intent.putExtra("fromSignUp", true)
                                    startActivity(intent)
                                    finish()

                                    var token = ""
                                    FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
                                        if (!task.isSuccessful) {
                                            Log.w(
                                                ContentValues.TAG,
                                                "Fetching FCM registration token failed",
                                                task.exception
                                            )
                                            return@addOnCompleteListener
                                        }
                                        token = task.result

                                        CoroutineScope(Dispatchers.IO).launch {
                                            saveTokenApp(token!!)
                                        }

                                        Log.d("token", token.toString())

                                    }
                                }
                                is State.Failed -> {
                                    loadingDialog.stopLoading()
                                    Snackbar.make(
                                        mBinding.root, result.error,
                                        Snackbar.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }
                    }
                } else {
                    Snackbar.make(
                        mBinding.root,
                        resources.getString(R.string.select_image),
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }


        }


    }

    private suspend fun saveTokenApp(token: String) {

        val auth = Firebase.auth
        val map: MutableMap<String, Any> = HashMap()
        map["appToken"] = token

        Firebase.database.getReference("Users")//.child("Patients")
            .child(auth.uid!!).updateChildren(map).await()

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == Constants.STORAGE_REQUEST_CODE) {
            //grantResults[0]--> because we want only the read externar storage to be checked
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImage()
            } else {
                Snackbar.make(
                    mBinding.root,
                    resources.getString(R.string.storage_permission_denied),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun pickImage() {

        CropImage.activity().setCropShape(CropImageView.CropShape.OVAL).start(this)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == RESULT_OK) {
                image = result.uri
                Glide.with(this).load(image).into(mBinding.imgPick)

            } else {
                Snackbar.make(
                    mBinding.root,
                    resources.getString(R.string.image_no_select),
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }

    }

    private fun areFieldsReady(): Boolean {
        with(mBinding) {
            name = edtUsername.text.trim().toString()
            phoneNumber = edtPhone.text.trim().toString()
            email = edtEmail.text.trim().toString()
            password = edtPassword.text.trim().toString()
            doctorliscence = edtDoctorLiscenceNumber.text.trim().toString()
        }
        var view: View? = null
        var flag = false

        when {
            name.isEmpty() -> {
                with(mBinding) {
                    edtUsername.error = resources.getString(R.string.field_required)
                    flag = true
                }
            }
            phoneNumber.isEmpty() -> {
                with(mBinding) {
                    edtPhone.error = resources.getString(R.string.field_required)
                    flag = true
                }
            }
            doctorliscence.isEmpty() -> {
                with(mBinding) {
                    edtDoctorLiscenceNumber.error = resources.getString(R.string.field_required)
                    flag = true

                }
            }
            email.isEmpty() -> {
                with(mBinding) {
                    edtEmail.error = resources.getString(R.string.field_required)
                    flag = true
                }
            }
            password.isEmpty() -> {
                with(mBinding) {
                    edtPassword.error = resources.getString(R.string.field_required)
                    flag = true
                }
            }
        }
        return if (flag) {
            view?.requestFocus()
            false
        } else true


    }

    override fun onStop() {
        super.onStop()
        //finish()

    }
}