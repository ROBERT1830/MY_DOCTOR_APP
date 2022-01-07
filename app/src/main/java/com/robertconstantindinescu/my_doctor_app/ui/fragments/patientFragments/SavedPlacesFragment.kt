package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters.SavedPlacesAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentSavedPlacesBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.NearLocationInterface
import com.robertconstantindinescu.my_doctor_app.interfaces.SavedPlaceInterface
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel
import com.robertconstantindinescu.my_doctor_app.models.placesModel.SavedPlaceModel
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class SavedPlacesFragment : Fragment(), NearLocationInterface {

    // TODO: 6/1/22 CREATE OFFLINE CACHE IF I HAVE TIME. For this one we have to store offline the firebase places and then to query the server again, we should store an Array with all the places id saved from firebase and check each and every time this array with the databas sql placces id. if there are differences then query the serverif not, show from sqlite.

    private lateinit var mBinding: FragmentSavedPlacesBinding
    private val locationViewModel: LocationViewModel by viewModels()

    private val mAdapter by lazy { SavedPlacesAdapter(this) }
    private lateinit var googlePlacesList: ArrayList<GooglePlaceModel>
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
        mBinding = FragmentSavedPlacesBinding.inflate(layoutInflater)


        loadingDialog = LoadingDialog(requireActivity())
        googlePlacesList = ArrayList()
        setUpRecyclerView()

        lifecycleScope.launchWhenStarted {
            getSavedPlaces()
        }


        return mBinding.root
    }

    private suspend fun getSavedPlaces() {
        loadingDialog.startLoading()
        googlePlacesList = locationViewModel.getSavedPlaces()

        if (!googlePlacesList.isNullOrEmpty()){
            loadingDialog.stopLoading()
            mAdapter.setUpAdapter(googlePlacesList)
        }else {
            loadingDialog.stopLoading()
            Toast.makeText(
                requireContext(),
                "No appointments requested at the moment",
                Toast.LENGTH_SHORT
            ).show()
        }

    }

    private fun setUpRecyclerView() {

        mBinding.savedPlacesRecyclerView.apply {
            adapter = mAdapter
            setHasFixedSize(true)
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

//    override fun onDirectionClick(savedPlaceModel: SavedPlaceModel) {
//
//        try {
//            val action = SavedPlacesFragmentDirections.actionBtnSavedPlacesToDirectionActivity(null, savedPlaceModel)
//        }catch (e:Exception){
//            Log.d("onDirectionClick", e.message.toString())
//        }
//    }

    override fun onSaveClick(googlePlaceModel: GooglePlaceModel) {
        return
    }

    override fun onDirectionClick(googlePlaceModel: GooglePlaceModel) {
        try {
            val action = SavedPlacesFragmentDirections.actionBtnSavedPlacesToDirectionActivity2(googlePlaceModel)
            findNavController().navigate(action)
        }catch (e:Exception){
            Log.d("onDirectionClick", e.message.toString())
        }
    }


}