package com.robertconstantindinescu.my_doctor_app.ui

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityPatientSignUpBinding
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.STORAGE_REQUEST_CODE
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView

class PatientSignUpActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityPatientSignUpBinding
    private lateinit var appPermissions: AppPermissions
    private lateinit var loadingDialog: LoadingDialog
    private var image: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPatientSignUpBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        appPermissions = AppPermissions()
        loadingDialog = LoadingDialog(this)

        mBinding.txtLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("isDoctor", false)
            startActivity(intent)
        }

        mBinding.imgPick.setOnClickListener {
            if (appPermissions.isStorageOk(this)) pickImage()
            else appPermissions.requestStoragePermission(this)
        }





        mBinding.btnSignUp.setOnClickListener {

            val intent = Intent(this, LoginActivity::class.java)
            intent.putExtra("isDoctor", false)
            startActivity(intent)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == STORAGE_REQUEST_CODE) {
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
}