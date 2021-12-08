package com.robertconstantindinescu.my_doctor_app.ui

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters.DirectionStepAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDirectionBinding
import com.robertconstantindinescu.my_doctor_app.databinding.BottomSheetLayoutBinding
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.directionPlaceModel.DirectionLegModel
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.directionPlaceModel.DirectionResponseModel
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.directionPlaceModel.DirectionRouteModel
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.LocationViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect

@AndroidEntryPoint
class DirectionActivity : AppCompatActivity(), OnMapReadyCallback {


    private lateinit var binding: ActivityDirectionBinding

    //pass the class generated with the argument needed.
    private val args by navArgs<DirectionActivityArgs>()

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
        binding = ActivityDirectionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //get arguments from bundle.
        endLat = args.googlePlaceModel.geometry!!.location!!.lat
        endLng = args.googlePlaceModel.geometry!!.location!!.lng

        placeId = args.googlePlaceModel.placeId!!

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        appPermissions = AppPermissions()
        loadingDialog = LoadingDialog(this)

        bottomSheetLayoutBinding = binding.bottomSheet
        //add behaviour to the bottom sheet when scrolling.
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayoutBinding.root)
        //init adapter
        adapterStep = DirectionStepAdapter()
        //set up the recycler. A linearLayout recycler.
        bottomSheetLayoutBinding.stepRecyclerView.apply {
            layoutManager = LinearLayoutManager(this@DirectionActivity)
            setHasFixedSize(false)
            //set the recycler adapter with the adapter we definned.
            adapter = adapterStep
        }

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.directionMap) as SupportMapFragment?
        mapFragment?.getMapAsync(this)

        binding.enableTraffic.setOnClickListener {
            if (isTrafficEnable) {
                mGoogleMap?.isTrafficEnabled = false
                isTrafficEnable = false
            } else {
                mGoogleMap?.isTrafficEnabled = true
                isTrafficEnable = true
            }
        }

        binding.travelMode.setOnCheckedChangeListener { _, checked ->
            if (checked != -1) {
                when (checked) {
                    R.id.btnChipDriving -> getDirection("driving")
                    R.id.btnChipWalking -> getDirection("walking")
                    R.id.btnChipBike -> getDirection("bicycling")
                    R.id.btnChipTrain -> getDirection("transit")
                }
            }
        }

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
                isLocationPermissionOk =
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                            && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true

                if (isLocationPermissionOk)
                    setUpGoogleMap()
                else
                    Snackbar.make(binding.root, "Location permission denied", Snackbar.LENGTH_LONG)
                        .show()

            }

    }


    override fun onMapReady(googleMap: GoogleMap) {

        mGoogleMap = googleMap

        when {
            ActivityCompat.checkSelfPermission(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                isLocationPermissionOk = true
                setUpGoogleMap()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                AlertDialog.Builder(this)
                    .setTitle("Location Permission")
                    .setMessage("Near me required location permission to access your location")
                    .setPositiveButton("Ok") { _, _ ->
                        requestLocation()
                    }.create().show()
            }

            else -> {
                requestLocation()
            }
        }

    }

    private fun setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        mGoogleMap?.isMyLocationEnabled = true
        mGoogleMap?.uiSettings?.isTiltGesturesEnabled = true
        mGoogleMap?.uiSettings?.isMyLocationButtonEnabled = false
        mGoogleMap?.uiSettings?.isCompassEnabled = false

        getCurrentLocation()

    }

    private fun getCurrentLocation() {
        val fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }
        //only use one because in the fragment you just asked for new location.
        fusedLocationProviderClient.lastLocation.addOnSuccessListener {

            if (it != null) {
                currentLocation = it
                getDirection("driving")
            } else {
                Toast.makeText(this, "Location not found", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun requestLocation() {
        permissionToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return super.onSupportNavigateUp()
    }

    //if we have the bottom sheet oppened and press the arrow to go bak to original map
    //the botthomseet wil be collapsed ass well. So for that chech the sate qhen the arroy is pressed.
    override fun onBackPressed() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED)
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        else
            super.onBackPressed()
    }

    private fun getDirection(mode: String) {
        if (isLocationPermissionOk) {
            val url = "https://maps.googleapis.com/maps/api/directions/json?" +
                    "origin=" + currentLocation.latitude + "," + currentLocation.longitude +
                    "&destination=" + endLat + "," + endLng +
                    "&mode=" + mode +
                    "&key=" + resources.getString(R.string.API_KEY)


            lifecycleScope.launchWhenStarted {
                locationViewModel.getDirection(url).collect {
                    when (it) {
                        is State.Loading -> {
                            if (it.flag == true)
                                loadingDialog.startLoading()
                        }

                        is State.Succes -> {
                            loadingDialog.stopLoading()
                            clearUI()

                            val directionResponseModel: DirectionResponseModel =
                                it.data as DirectionResponseModel

                            val routeModel: DirectionRouteModel =
                                directionResponseModel.directionRouteModels!![0]

                            supportActionBar!!.title = routeModel.summary
                            //we dont do like before because,legs could be null
                            val legModel: DirectionLegModel = routeModel.legs?.get(0)!!
                            binding.apply {
                                txtStartLocation.text = legModel.startAddress
                                txtEndLocation.text = legModel.endAddress
                            }
                            bottomSheetLayoutBinding.apply {
                                txtSheetTime.text = legModel.duration?.text
                                txtSheetDistance.text = legModel.distance?.text
                            }
                            //now that we have the data seted up lets call the adapter
                            //to show the directions. So we will pass the steps. wich is a list
                            //of directions that in tunr are objects that holds many information
                            adapterStep.setDirectionStepModels(legModel.steps!!)

                            //crete a step list in which we will have pair lat lng with each and every
                            //location between the start and end point.
                            val stepList: MutableList<LatLng> = ArrayList()
                            //define the line style that bind the locations
                            val options = PolylineOptions().apply {
                                width(25f)
                                color(Color.BLUE)
                                geodesic(true)
                                clickable(true)
                                visible(true)
                            }
                            //get a partern for the line. This will be a list of different patterns
                            //according to the type of navigation
                            val pattern: List<PatternItem>
                            if (mode == "walking") {
                                //a pattern of dots with 10f of distance between them
                                pattern = listOf(
                                    Dot(), Gap(10f)
                                )
                                /*Type of join when there is a change in direction for example
                                * I mean the vertex between one direction and the other one
                                * between start and end. */
                                options.jointType(JointType.ROUND)
                            } else {
                                //dash is largen than a dot. It lenght is specified.
                                pattern = listOf(
                                    //damos longitug al dash.
                                    Dash(30f)
                                )

                            }
                            //define the patterns according to the navigation mode.
                            options.pattern(pattern)
                            for (stepModel in legModel.steps) {
                                //decodificamos la polilinea. cada uno de los puntos par aobtener la lat y lng
                                val decodedList = decode(stepModel.polyline?.points!!)
                                //por cada punto decodificado añadiremos a la lista de pasos esas hubicaciones
                                //con pares lat, long.
                                for (latLng in decodedList) {
                                    //añades a la lista de poares long lat los valores.
                                    stepList.add(
                                        LatLng(
                                            latLng.lat,
                                            latLng.lng
                                        )
                                    )
                                }
                            }
                            //add to all steps the style.
                            options.addAll(stepList)
                            //add the polyline to the map. That poliline is formated and styled
                            //with al the directions. So after that we will add the start and en location
                            //to have two point joined by the polyline
                            mGoogleMap?.addPolyline(options)
                            //add the start point
                            val startLocation = com.google.android.gms.maps.model.LatLng(
                                legModel.startLocation?.lat!!,
                                legModel.startLocation.lng!!
                            )
                            //add end location
                            val endLocation = com.google.android.gms.maps.model.LatLng(
                                legModel.endLocation?.lat!!,
                                legModel.endLocation.lng!!
                            )
                            //add marker to the end location
                            mGoogleMap?.addMarker(
                                MarkerOptions()
                                    .position(endLocation)
                                    .title("End Location")
                            )
                            //add marker to start location
                            mGoogleMap?.addMarker(
                                MarkerOptions()
                                    .position(startLocation)
                                    .title("Start Location")
                            )

                            /*build a rectangle to be able the camera to foccus on and see the camera
                            * of google with both locations*/
                            val builder = LatLngBounds.builder()
                            builder.include(endLocation).include(startLocation)
                            //build th bounds
                            val latLngBounds = builder.build()

                            //animate camara to that bound defined above
                            mGoogleMap?.animateCamera(
                                CameraUpdateFactory.newLatLngBounds(
                                    latLngBounds, 0
                                )
                            )
                        }
                        is State.Failed -> {
                            loadingDialog.stopLoading()
                            Snackbar.make(
                                binding.root, it.error,
                                Snackbar.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        }


    }

    /**
     * When we ask for a direction, maybe we have somethin printed int eh map like trafiic or infoadapter
     * with some info. So we will clear the map.
     *
     */
    private fun clearUI() {
        mGoogleMap?.clear()
        //set the text to empty string in both start and end location
        binding.txtStartLocation.text = ""
        binding.txtEndLocation.text = ""
        //the title of the action bar to empty string.
        supportActionBar!!.title = ""
        //the text of the bottom sheet that references you with the distante and time will be empty
        //strings ass well.
        bottomSheetLayoutBinding.txtSheetDistance.text = ""
        bottomSheetLayoutBinding.txtSheetTime.text = ""
    }

    private fun decode(points: String): List<com.google.maps.model.LatLng> {
        val len = points.length
        val path: MutableList<com.google.maps.model.LatLng> = java.util.ArrayList(len / 2)
        var index = 0
        var lat = 0
        var lng = 0
        while (index < len) {
            var result = 1
            var shift = 0
            var b: Int
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lat += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            result = 1
            shift = 0
            do {
                b = points[index++].code - 63 - 1
                result += b shl shift
                shift += 5
            } while (b >= 0x1f)
            lng += if (result and 1 != 0) (result shr 1).inv() else result shr 1
            path.add(com.google.maps.model.LatLng(lat * 1e-5, lng * 1e-5))
        }
        return path
    }


}