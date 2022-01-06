package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.bumptech.glide.Glide
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentSettingsBinding
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.SettingsViewModel
import com.theartofdev.edmodo.cropper.CropImage
import com.theartofdev.edmodo.cropper.CropImageView
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class SettingsFragment : Fragment() {

    private lateinit var mBinding: FragmentSettingsBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var appPermissions: AppPermissions
    private var image: Uri? = null
    private val settingsViewModel: SettingsViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionToRequest = mutableListOf<String>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentSettingsBinding.inflate(layoutInflater)
        firebaseAuth = Firebase.auth
        loadingDialog = LoadingDialog(requireActivity())
        appPermissions = AppPermissions()

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                val isStoragePermissionOk =
                    permissions[android.Manifest.permission.READ_EXTERNAL_STORAGE] == true
                            && permissions[android.Manifest.permission.WRITE_EXTERNAL_STORAGE] == true

                if (isStoragePermissionOk) {
                    pickImage()
                } else Snackbar.make(
                    mBinding.root,
                    resources.getString(R.string.storage_perm_denies),
                    Snackbar.LENGTH_SHORT
                ).show()

            }

        mBinding.imgCamera.setOnClickListener {
            if (appPermissions.isStorageOk(requireContext())){
                pickImage()
            }else{
                requestStorage()
            }
        }

        mBinding.txtUsername.setOnClickListener { usernameDialog() }

        mBinding.cardEmail.setOnClickListener {


            val action = SettingsFragmentDirections.actionBtnSettingsToEmailConfirmationFragment()
            Navigation.findNavController(requireView()).navigate(action)

        }

        mBinding.cardPassword.setOnClickListener {
            val direction = SettingsFragmentDirections.actionBtnSettingsToEmailConfirmationFragment(isPassword = true)
            Navigation.findNavController(requireView()).navigate(direction)
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Settings"

        mBinding.apply {
            txtEmail.text = firebaseAuth.currentUser?.email
            txtUsername.text = firebaseAuth.currentUser?.displayName

            Glide.with(requireContext()).load(firebaseAuth.currentUser?.photoUrl)
                .into(imgProfile)
        }
    }

    private fun usernameDialog() {

        val builder = AlertDialog.Builder(requireContext())

        val view: View = LayoutInflater.from(requireContext())
            .inflate(R.layout.username_dialog_layout, null, false)
        //build the alert with de view.
        builder.setView(view)
        //reference to the edit username view.
        val edtUsername: TextInputEditText = view.findViewById(R.id.edtDialogUsername)
        //build the alert with a title. and a positive button.
        builder.setTitle("Edit Username")
            .setPositiveButton("Save") { _, _ ->

                //get the name from the inputEditText when the user has entered something.
                val name = edtUsername.text?.trim().toString()
                //if the user has entered something.
                if (name.isNotEmpty())
                    updateName(name)
                else {
                    Toast.makeText(requireContext(), "Username is required", Toast.LENGTH_SHORT)
                        .show()
                }
            }
            .setNegativeButton("Cancel") { _, _ -> }.create().show()

    }

    private fun updateName(name: String) {
        lifecycleScope.launchWhenStarted {
            settingsViewModel.updateName(name).collect {
                when (it) {
                    is State.Loading -> {
                        if (it.flag == true) {
                            loadingDialog.startLoading()
                        }
                    }
                    is State.Succes -> {
                        loadingDialog.stopLoading()
                        //set the textusername
                        mBinding.txtUsername.text = name
                        //inform the user of succeful update.
                        Snackbar.make(
                            mBinding.root, it.data.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()

                    }
                    is State.Failed -> {
                        loadingDialog.stopLoading()
                        Snackbar.make(
                            mBinding.root, it.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun requestStorage() {
        permissionToRequest.add(android.Manifest.permission.READ_EXTERNAL_STORAGE)
        permissionToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)

        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun pickImage() {
        CropImage.activity()
            .setCropShape(CropImageView.CropShape.OVAL)
            .start(requireContext(), this)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            val result = CropImage.getActivityResult(data)
            if (resultCode == Activity.RESULT_OK) {
                image = result.uri

                updateImage(image!!)
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Snackbar.make(mBinding.root, "${result.error}", Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun updateImage(image: Uri) {
        lifecycleScope.launchWhenStarted {
            settingsViewModel.updateImage(image).collect {
                when (it) {
                    is State.Loading -> {
                        if (it.flag == true) {
                            loadingDialog.startLoading()
                        }
                    }

                    is State.Succes -> {
                        //once the image is updated...
                        loadingDialog.stopLoading()
                        //load the image into the imageView.
                        Glide.with(requireContext()).load(image).into(mBinding.imgProfile)
                        Snackbar.make(
                            mBinding.root, it.data.toString(),
                            Snackbar.LENGTH_SHORT
                        ).show()


                    }
                    is State.Failed -> {
                        loadingDialog.stopLoading()
                        Snackbar.make(
                            mBinding.root, it.error,
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }


    }

}