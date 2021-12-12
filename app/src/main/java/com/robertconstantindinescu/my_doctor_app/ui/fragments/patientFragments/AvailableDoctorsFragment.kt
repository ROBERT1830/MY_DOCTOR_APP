package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AvailableDoctorsAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAvailableDoctorsBinding
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.AvailableDoctorsViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AvailableDoctorsFragment : Fragment() {

    private lateinit var mBinding: FragmentAvailableDoctorsBinding
    private val availableDoctorsViweModel: AvailableDoctorsViewModel by viewModels<AvailableDoctorsViewModel>()
    private val mAdapter by lazy { AvailableDoctorsAdapter() }

    private lateinit var availableDoctors: ArrayList<DoctorModel>
    private lateinit var loadingDialog: LoadingDialog


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
        mBinding = FragmentAvailableDoctorsBinding.inflate(inflater, container, false)
        loadingDialog = LoadingDialog(requireActivity())
        availableDoctors = ArrayList<DoctorModel>()

        setUpRecyclerView()

        lifecycleScope.launchWhenStarted {
            loadingDialog.startLoading()
            availableDoctors = availableDoctorsViweModel.getAvailableDoctors()
            if (!availableDoctors.isNullOrEmpty()){
                loadingDialog.stopLoading()
                mAdapter.setUpAdapter(availableDoctors)
            }


//            availableDoctorsViweModel.getAvailableDoctors().collect { result ->
//                when (result) {
//                    is State.Loading -> {
//                        if (result.flag == true) loadingDialog.startLoading()
//                    }
//                    is State.Succes -> {
//                        loadingDialog.stopLoading()
//                        availableDoctors.add(result.data as DoctorModel)
//                        mAdapter.setUpAdapter(availableDoctors)
//                    }
//                    is State.Failed -> {
//                        loadingDialog.stopLoading()
//                        Snackbar.make(
//                            mBinding.root, result.error,
//                            Snackbar.LENGTH_SHORT
//                        ).show()
//
//                    }
//                }



        }

        return mBinding.root


    }

    private fun setUpRecyclerView() {

        mBinding.recyclerViewAvailableDoctors.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)

        }


    }


}

































