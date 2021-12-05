package com.robertconstantindinescu.my_doctor_app.ui.fragments

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.os.Looper
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.res.ResourcesCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters.InfoWindowAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentMapsBinding
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.placesName
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import kotlinx.android.synthetic.main.fragment_maps.*
import java.security.Permission


class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {


    private lateinit var binding: FragmentMapsBinding
    private var mGoogleMap: GoogleMap? = null

    //private lateinit var appPermission: AppPermissions
    private lateinit var loadingDialog: LoadingDialog
    private lateinit var permissionLauncher: ActivityResultLauncher<Array<String>>
    private var permissionToRequest = mutableListOf<String>()
    private lateinit var appPermission: AppPermissions
    private var isLocationPermissionOk = false

    private lateinit var locationRequest: LocationRequest
    private lateinit var locationCallback: LocationCallback
    private var fusedLocationProviderClient: FusedLocationProviderClient? = null
    private lateinit var currentLocation: Location
    private var infoWindowAdapter: InfoWindowAdapter? = null
    private var currentMarker:  Marker? = null

    private  var isTrafficEnable: Boolean = false


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

        binding = FragmentMapsBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPermission = AppPermissions()
        loadingDialog = LoadingDialog(requireActivity())

        /**registerForActivityResult - Take an activityResultContract with the multiple permission
         * which are sent to an other activity from system to check or not the permissions for the
         * app. Takes  a ActivityResultCallBack  in a lambda format wich will have the result back
         * from those permissions granted. */

        permissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

                isLocationPermissionOk =
                    permissions[android.Manifest.permission.ACCESS_COARSE_LOCATION] == true
                            && permissions[android.Manifest.permission.ACCESS_FINE_LOCATION] == true

                // TODO: 29/11/21
                if (isLocationPermissionOk) setUpGoogleMap()
                else Snackbar.make(
                    binding.root,
                    R.string.location_permission_denied,
                    Snackbar.LENGTH_SHORT
                )

            }

        /**Instanciate the fragment that holds the map*/
        val mapFragment =
            (childFragmentManager.findFragmentById(R.id.MapsFragmentMap) as SupportMapFragment)
        mapFragment?.getMapAsync(this)

        /**Create the chipGroup from the array of palces in constants*/
        for (placeModel in Constants.placesName) {
            val chip = Chip(requireContext())
            chip.text = placeModel.name
            chip.id = placeModel.id
            chip.setPadding(8, 8, 8, 8)
            chip.setTextColor(resources.getColor(R.color.white, null))
            chip.chipBackgroundColor = resources.getColorStateList(R.color.primaryColor, null)
            chip.chipIcon = ResourcesCompat.getDrawable(resources, placeModel.drawableId, null)
            chip.isCheckable = true
            chip.isCheckedIconVisible = false
            binding.placesGroup.addView(chip)


        }
        /**Enable traffic realtime on the map*/
        binding.enableTraffic.setOnClickListener {
            if (isTrafficEnable){
                mGoogleMap?.apply {
                    isTrafficEnabled = false
                    isTrafficEnable = false
                }
            }else
                mGoogleMap?.apply {
                    isTrafficEnabled = true
                    isTrafficEnable = true
                }
        }
        binding.currentLocation.setOnClickListener { getCurrentLocation() }

        binding.btnMapType.setOnClickListener {
            val popUpMenu = PopupMenu(requireContext(), it)
            popUpMenu.apply {
                menuInflater.inflate(R.menu.map_type_menu, popUpMenu.menu)

                setOnMenuItemClickListener { item ->
                    when(item.itemId){
                        R.id.btnNormal -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                        R.id.btnSatellite -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        R.id.btnTerrain -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN

                    }
                    true

                }
                show()
            }

        }





    }

    /**
     * After the permissions are accepted the map is getting ready and this method is called.
     *
     * ---> lo que sucede es lo siguiente. Cuando venimos a este fragment, permissionLauncher al inicio
     * es null. Entonces directamente se llama a getMapAsync y cuadno el mapa está listo
     * se hace un callback a onMapReady. Aquí pasamos por las diferentes opciones y como no
     * se haaceptado ningun se mete en el else para cargar en el mutable list los permisos que queremos
     * y con el permissionLauncher, lanzarlos y pedirlos.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        mGoogleMap = googleMap
        when {
            ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED -> {
                isLocationPermissionOk = true
                setUpGoogleMap()
            }

            ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) -> {
                AlertDialog.Builder(requireContext())
                    .setTitle(resources.getString(R.string.location_permission_title))
                    .setMessage(resources.getString(R.string.location_permission_message))
                    .setPositiveButton("OK") { _, _ -> requestLocation() }.create().show()
            }
            else -> {
                requestLocation()
            }


        }


    }

    /*A mutable list has some method like add and remove so its size can be changd.
    * Instead, arrays its size is predefined. moreover Mutablelist is an interface that has different
    * types but array is a class. So we have to swith mutableList to an array of the same type.
    * Because of that we use toTypedArray function*/

    /**Method to request again the permissions using the permissionLauncher wich is a type of
     * ActivityResultLauncher that launches a contract. */
    private fun requestLocation() {
        permissionToRequest.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        permissionToRequest.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)

        permissionLauncher.launch(permissionToRequest.toTypedArray())
    }

    private fun setUpGoogleMap() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return

        }

        //Enables or disables the my-location layer
        //enables a layoer that contains the button to center your locattion and the current direction
        mGoogleMap?.isMyLocationEnabled = true
        //Gets the user interface settings for the map and able the tilGesture.
        mGoogleMap?.uiSettings?.isTiltGesturesEnabled = true
        mGoogleMap?.setOnMarkerClickListener(this)

        //set up the settings for the locaiton updates.
        setUpLocationUpdate()


    }

    /**
     * Method that sets up the pre-requisites for the lication request.
     * like interval (app updates with the lcation), fastestInterval (location update via gps) and
     * priority (via gps for hy accuracy)
     */
    private fun setUpLocationUpdate() {
        //create the object
        locationRequest = LocationRequest.create()
        locationRequest.interval = 10000
        locationRequest.fastestInterval = 5000
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY

        locationCallback = object : LocationCallback() {

            override fun onLocationResult(locationResult: LocationResult) {
                super.onLocationResult(locationResult)
                for (location in locationResult?.locations) {
                    Log.d("TAG", "onLocationResult: ${location.longitude} ${location.latitude}")
                }
            }
        }

        //create the fusedProviderClient
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext())

        startLocationUpdates()

    }

    /**
     * Métod that gets an updated location (requesLocationUpdates). we will get that with the fusedLocationProvider
     * that we instanciate before.
     * wITH THIS METHOD WE ASK FOR A NEW BRAND UPDATED LOCATION
     */
    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }

        //can be null because for example we don't have internet.
        fusedLocationProviderClient?.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), R.string.location_update, Toast.LENGTH_SHORT)
            }

        }

        getCurrentLocation()

    }

    /**
     * Method that will get the current location or the last location asked for the fusedlocationprovider
     * and call the camer maps to move forward that lcoation.
     */
    private fun getCurrentLocation() {
        //create a new object of fusedLoactionProviderClient to ask the laslocation which has been
        //provided by an other fusedLoactionProviderClient.

        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireContext()) //for what activity will be asking

        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(
                requireContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            isLocationPermissionOk = false
            return
        }

        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
            currentLocation = location
            //infoWindowAdapter = null

            infoWindowAdapter = InfoWindowAdapter(currentLocation, requireContext())
            mGoogleMap?.setInfoWindowAdapter(infoWindowAdapter)
            moveCamaraToLocation(currentLocation)

        }.addOnFailureListener {
            Toast.makeText(requireContext(), "$it", Toast.LENGTH_SHORT).show()
        }


    }

    private fun moveCamaraToLocation(currentLocation: Location?) {

        var camaraUpdate = CameraUpdateFactory.newLatLngZoom(
            LatLng(
                currentLocation!!.latitude,
                currentLocation!!.longitude
            ), 17f
        )
        //set the position and other settings for marker.
        val markerOption =
            MarkerOptions().position(LatLng(currentLocation.latitude, currentLocation.longitude))
                .title("Current location")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                //.snippet(firebaseAuth.currentUser?.displayName)

        currentMarker?.remove()

        currentMarker = mGoogleMap?.addMarker(markerOption)
        currentMarker?.tag = 703

        mGoogleMap?.animateCamera(camaraUpdate)


    }

    fun stopLocationUpdates(){
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if(fusedLocationProviderClient!= null){
            startLocationUpdates()
            //remove the current marker becasue it could have been changed.
            currentMarker?.remove()
        }
    }





    override fun onMarkerClick(p0: Marker): Boolean {
        return true

    }
}