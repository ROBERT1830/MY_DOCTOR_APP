package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.settings

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.Navigation
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPasswordChangeBinding
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class PasswordChangeFragment : Fragment() {

    private lateinit var mBinding: FragmentPasswordChangeBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var password: String
    private val settingsViewModel:SettingsViewModel  by viewModels()


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
        mBinding = FragmentPasswordChangeBinding.inflate(inflater, container, false)
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "New Password"
        loadingDialog = LoadingDialog(requireActivity())
        return mBinding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mBinding.btnUpdatePassword.setOnClickListener {
            if (areFieldReady()) {
                lifecycleScope.launchWhenStarted {
                    settingsViewModel.updatePassword(password).collect {
                        when (it) {
                            is State.Loading -> {
                                if (it.flag == true)
                                    loadingDialog.startLoading()
                            }

                            is State.Succes-> {
                                loadingDialog.stopLoading()
                                Snackbar.make(
                                    mBinding.root,
                                    it.data.toString(),
                                    Snackbar.LENGTH_SHORT
                                ).show()

                                Navigation.findNavController(requireView())
                                    .popBackStack()

                            }
                            is State.Failed -> {
                                loadingDialog.stopLoading()
                                Snackbar.make(
                                    mBinding.root,
                                    it.error,
                                    Snackbar.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        }
    }






    private fun areFieldReady(): Boolean {


        password = mBinding.edtUPassword.text?.trim().toString()

        var view: View? = null
        var flag = false

        when {

            password.isEmpty() -> {
                mBinding.edtUPassword.error = "Field is required"
                view = mBinding.edtUPassword
                flag = true
            }
            password.length < 8 -> {
                mBinding.edtUPassword.error = "Minimum 8 characters"
                view = mBinding.edtUPassword
                flag = true
            }
        }

        return if (flag) {
            view?.requestFocus()
            false
        } else
            true

    }


}