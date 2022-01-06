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
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentEmailConfirmationBinding
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.SettingsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class EmailConfirmationFragment : Fragment() {

    private lateinit var binding: FragmentEmailConfirmationBinding
    private lateinit var emailFragmentArgs: EmailConfirmationFragmentArgs

    private var isPassword = false
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var firebaseAuth: FirebaseAuth
    private val settingsViewModel: SettingsViewModel by viewModels()

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
        binding =
            FragmentEmailConfirmationBinding.inflate(inflater, container, false)

        //modify the actionbar title.
        (activity as AppCompatActivity?)!!.supportActionBar?.title = "Email Confirmation"
        //get the argumnents from bundle.
        emailFragmentArgs = EmailConfirmationFragmentArgs.fromBundle(requireArguments())
        //asign the bundle argo bolean to the global vaiable.
        isPassword = emailFragmentArgs.isPassword
        firebaseAuth = Firebase.auth
        loadingDialog = LoadingDialog(requireActivity())

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        //set the text to the email text from firebase.
        binding.edtCEmail.setText(firebaseAuth.currentUser?.email)


        binding.btnConfirmAccount.setOnClickListener {
            if (areFieldReady()) {
                //get the credentials. wE USE THE EmailAuthProvider that represents the
                //email and password auth mechanism.
                val authCredential = EmailAuthProvider
                    .getCredential(email, password)

                lifecycleScope.launchWhenStarted {
                    settingsViewModel.confirmEmail(authCredential).collect {
                        when (it) {
                            is State.Loading -> {
                                if (it.flag == true)
                                    loadingDialog.startLoading()
                            }

                            is State.Succes -> {
                                loadingDialog.stopLoading()
                                if (isPassword) {
                                    //if we came from password card move to passwordchange fragment.
                                    //to get your password chnged we ned to re auth the user.
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_emailConfirmationFragment_to_passwordChangeFragment)

                                }else{
                                    //is is false go to email.
                                    Navigation.findNavController(view)
                                        .navigate(R.id.action_emailConfirmationFragment_to_emailChangeFragment)

                                }

                            }
                            is State.Failed -> {
                                loadingDialog.stopLoading()
                                Snackbar.make(
                                    binding.root,
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

        //aign to gloabl valriable the content of the views.
        email = binding.edtCEmail.text?.trim().toString()
        password = binding.edtCPassword.text?.trim().toString()

        var view: View? = null
        var flag = false //means is not empty

        when {

            email.isEmpty() -> {
                binding.edtCEmail.error = "Field is required"
                view = binding.edtCEmail
                flag = true
            }
            password.isEmpty() -> {
                binding.edtCPassword.error = "Field is required"
                view = binding.edtCPassword
                flag = true
            }
            password.length < 8 -> {
                binding.edtCPassword.error = "Minimum 8 characters"
                view = binding.edtCPassword
                flag = true
            }
        }

        //if is empty one view rquest focus and retun false.
        return if (flag) {
            view?.requestFocus()
            false
        } else
            true

    }


}