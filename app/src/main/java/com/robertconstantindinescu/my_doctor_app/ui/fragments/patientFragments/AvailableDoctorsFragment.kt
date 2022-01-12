package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.AvailableDoctorsAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAvailableDoctorsBinding
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.utils.CheckInternet
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.NetworkListener
import com.robertconstantindinescu.my_doctor_app.viewmodels.AvailableDoctorsViewModel
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesQueryUtilsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class AvailableDoctorsFragment : Fragment() {

    private lateinit var mBinding: FragmentAvailableDoctorsBinding
    private val availableDoctorsViweModel: AvailableDoctorsViewModel by viewModels<AvailableDoctorsViewModel>()
    private val mAdapter by lazy { AvailableDoctorsAdapter() }

    private lateinit var availableDoctors: ArrayList<DoctorModel>
    private lateinit var loadingDialog: LoadingDialog

    private lateinit var swipeRefreshLayout: SwipeRefreshLayout

//    private lateinit var recipesQueryUtilsViewModel: RecipesQueryUtilsViewModel
//    private lateinit var networkListener: NetworkListener


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
        swipeRefreshLayout = mBinding.swipeRefreshRecycler

        setUpRecyclerView()

        if(CheckInternet.hasInternetConnection(requireContext())){
            lifecycleScope.launchWhenStarted {
                getAvailableDoctors()
            }

        }else{
            Toast.makeText(
                requireContext(),
                resources.getString(R.string.no_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }

        return mBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        swipeRefreshLayout.setOnRefreshListener {
            if (swipeRefreshLayout.isRefreshing) {
                swipeRefreshLayout.isRefreshing = false
            }
            if(CheckInternet.hasInternetConnection(requireContext())){
                lifecycleScope.launchWhenStarted {
                    getAvailableDoctors()
                    Log.d("requestedDoctorAppointmentsList", availableDoctors.toString())
                }
            }else{
                Toast.makeText(
                    requireContext(),
                    resources.getString(R.string.no_internet_connection),
                    Toast.LENGTH_SHORT
                ).show()
            }

        }

    }

    private suspend fun getAvailableDoctors() {
        loadingDialog.startLoading()
        availableDoctors = availableDoctorsViweModel.getAvailableDoctors()
        if (!availableDoctors.isNullOrEmpty()){
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(availableDoctors)
        }else {
            loadingDialog.stopLoading()
            Toast.makeText(
                requireContext(),
                "No available doctors at the moment",
                Toast.LENGTH_SHORT
            ).show()
        }
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

































