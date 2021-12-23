package com.robertconstantindinescu.my_doctor_app.ui

import android.app.AlertDialog
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityPatientVideoCallBinding
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.ROOM_CODE
import io.agora.agorauikit_android.AgoraConnectionData
import io.agora.agorauikit_android.AgoraVideoViewer

class PatientVideoCallActivity : AppCompatActivity() {

    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var isCameraAndMicrophoneGranted = false
    private lateinit var videoCallPermissions: AppPermissions
    private var permissionToRequest = mutableListOf<String>()
    private lateinit var mBinding: ActivityPatientVideoCallBinding
    var agView: AgoraVideoViewer? = null




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityPatientVideoCallBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        val _intent = intent
        val roomCode = _intent.getStringExtra(ROOM_CODE)


        mBinding.edTextRoomCode.setText(roomCode)
        //Log.d("roomCode", roomCode.toString())

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                isCameraAndMicrophoneGranted =
                    permissions[android.Manifest.permission.CAMERA] == true
                            && permissions[android.Manifest.permission.RECORD_AUDIO] == true

                if (isCameraAndMicrophoneGranted) {
                    startVideoCall()
                } else Snackbar.make(
                    mBinding.root,
                    "Camera and microhone permission denied",
                    Snackbar.LENGTH_SHORT
                ).show()
            }

        mBinding.btnCall.setOnClickListener {

            setUpVideoCall()
        }
    }

    @ExperimentalUnsignedTypes
    private fun setUpVideoCall() {

        when {
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) == PackageManager.PERMISSION_GRANTED -> {
                startVideoCall()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.RECORD_AUDIO
            ) -> {
                AlertDialog.Builder(this)
                    .setTitle("Microphone Perission")
                    .setMessage("The application requires microphone and camera permission to perform a video call")
                    .setPositiveButton("OK") { _, _ -> requestMicrophonePermission() }.create().show()
            }
            else -> {
                requestMicrophonePermission()
            }
        }



    }

    private fun requestMicrophonePermission() {

        permissionToRequest.add(android.Manifest.permission.CAMERA)
        permissionToRequest.add(android.Manifest.permission.RECORD_AUDIO)

        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun startVideoCall() {
        val roomCode = mBinding.edTextRoomCode.text.toString().trim()
        agView = AgoraVideoViewer(this, AgoraConnectionData(Constants.APP_ID))
        this.addContentView(
            agView,
            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        )
        agView!!.join(roomCode)
    }
}