package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebViewClient
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentInstructionsBinding
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.Result
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RECIPE_RESULT_KEY
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog


class InstructionsFragment : Fragment() {

    private lateinit var mBinding: FragmentInstructionsBinding
    private var myBundle: Result? = null
    private var loadingDialog: LoadingDialog? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myBundle = it.getParcelable(RECIPE_RESULT_KEY)
        }
        loadingDialog  = LoadingDialog(requireActivity())
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentInstructionsBinding.inflate(layoutInflater)

        mBinding.instructionsWebView.webViewClient = object : WebViewClient(){}
        val websiteUrl: String = myBundle!!.sourceUrl!!
        loadingDialog!!.startLoading()
        mBinding.instructionsWebView.loadUrl(websiteUrl)
        loadingDialog!!.stopLoading()

        return mBinding.root
    }


}