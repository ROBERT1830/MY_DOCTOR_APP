package com.robertconstantindinescu.my_doctor_app.ui

import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.viewModels
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters.DirectionStepAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDirectionBinding
import com.robertconstantindinescu.my_doctor_app.databinding.BottomSheetLayoutBinding
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.viewmodels.LocationViewModel

class DirectionActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var binding: ActivityDirectionBinding

    private var mGoogleMap: GoogleMap? = null

    private lateinit var appPermissions: AppPermissions
    private var isLocationPermissionOk = false
    private var isTrafficEnable = false

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<RelativeLayout>
    //binding with the bottomsheet layout.
    private lateinit var bottomSheetLayoutBinding: BottomSheetLayoutBinding
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var currentLocation: Location
    //variables for end lat and longitude this will be filled with the intent data that we received.
    private var endLat: Double? = null
    private var endLng: Double? = null
    private lateinit var placeId: String

    private lateinit var adapterStep: DirectionStepAdapter
    private val locationViewModel: LocationViewModel by viewModels()
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionToRequest = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_direction)
    }

    override fun onMapReady(p0: GoogleMap) {
        TODO("Not yet implemented")
    }
}