package com.robertconstantindinescu.my_doctor_app.ui.fragments

import android.app.AlertDialog
import android.content.pm.PackageManager
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.graphics.Bitmap
import android.graphics.Canvas
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
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.PagerSnapHelper
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SnapHelper
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.*
import com.google.android.material.chip.Chip
import com.google.android.material.snackbar.BaseTransientBottomBar
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters.GooglePlaceAdapter
import com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters.InfoWindowAdapter

import com.robertconstantindinescu.my_doctor_app.databinding.FragmentMapBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.NearLocationInterface
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GoogleResponseModel
import com.robertconstantindinescu.my_doctor_app.utils.AppPermissions
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.placesName
import com.robertconstantindinescu.my_doctor_app.utils.LoadingDialog
import com.robertconstantindinescu.my_doctor_app.utils.State
import com.robertconstantindinescu.my_doctor_app.viewmodels.LocationViewModel
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_map.*
import kotlinx.coroutines.flow.collect
import java.security.Permission

@AndroidEntryPoint
class MapsFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener,
    NearLocationInterface {


    private lateinit var binding: FragmentMapBinding
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
    private var currentMarker: Marker? = null

    private var isTrafficEnable: Boolean = false
    private var radius = 4500.00

    private lateinit var googlePlaceList: ArrayList<GooglePlaceModel>
    private var userSavedLocationId: ArrayList<String> = ArrayList()

    private val locationViewModel: LocationViewModel by viewModels()

    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var googleplaceAdapter: GooglePlaceAdapter


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

        binding = FragmentMapBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        appPermission = AppPermissions()
        loadingDialog = LoadingDialog(requireActivity())
        firebaseAuth = Firebase.auth
        googlePlaceList = ArrayList()


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
            if (isTrafficEnable) {
                mGoogleMap?.apply {
                    isTrafficEnabled = false
                    isTrafficEnable = false
                }
            } else
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
                    when (item.itemId) {
                        R.id.btnNormal -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_NORMAL
                        R.id.btnSatellite -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_SATELLITE
                        R.id.btnTerrain -> mGoogleMap?.mapType = GoogleMap.MAP_TYPE_TERRAIN

                    }
                    true

                }
                show()
            }

        }

        binding.placesGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId != -1) {
                val placemodel = placesName[checkedId - 1]
                binding.edtPlaceName.setText(placemodel.name)

                getNearByPlaces(placemodel.placeType)
            }
        }

        setUpRecyclerView()
        //get the places that the user have saved in a previous session.
        /*we will get this saved places from firebase database named Saved Locations*/
        lifecycleScope.launchWhenStarted {
            //this one here is super important because due to that when we launch the app,
            //we get all the places saved in the firebase for a given logged user. So that
            //we will have them stored in that string arrayList and watch all of them
            //once we o to a particular chip and press it.
            userSavedLocationId = locationViewModel.getUserLocationId()
            Log.d("TAG", "onViewCreated: ${userSavedLocationId.size}")
        }


    }


    private fun getNearByPlaces(placeType: String) {

        val url = ("https://maps.googleapis.com/maps/api/place/nearbysearch/json?location="
                + currentLocation.latitude + "," + currentLocation.longitude
                + "&radius=" + radius + "&type=" + placeType + "&key=" +
                resources.getString(R.string.API_KEY))

        lifecycleScope.launchWhenStarted {
            locationViewModel.getNearByPlace(url).collect {
                when (it) {
                    is State.Loading -> {
                        if (it.flag == true) {
                            loadingDialog.toString()
                        }
                    }
                    is State.Succes -> {
                        loadingDialog.stopLoading()
                        val googleResponseModel: GoogleResponseModel =
                            it.data as GoogleResponseModel
                        //if the response with the googlePlaceModelList is not null and not empty
                        //then clear the map and add new markers.
                        if (googleResponseModel.googlePlaceModelList !== null &&
                            googleResponseModel.googlePlaceModelList.isNotEmpty()
                        ) {

                            googlePlaceList.clear()
                            mGoogleMap?.clear()

                            //for each googlePlaceModel check is it is save and add it to the googleplaceList
                            for (i in googleResponseModel.googlePlaceModelList.indices) {
                                //change the save transient property of googlePlaceModelList(obtained above from firebase)
                                //to true or false depending on the userSavedLocationId arrayLiast
                                //if that contains the placeId.
                                googleResponseModel.googlePlaceModelList[i].saved =
                                    userSavedLocationId.contains(googleResponseModel.googlePlaceModelList[i].placeId)
                                googlePlaceList.add(googleResponseModel.googlePlaceModelList[i])
                                //add marker for each place that we get the response.
                                addMarker(googleResponseModel.googlePlaceModelList[i], i)
                            }
                            // TODO: 5/12/21 do the adapter. and the else.  and the failsed state
                            googleplaceAdapter.setGooglePlaces(googlePlaceList)

                        } else {
                            mGoogleMap?.clear()
                            googlePlaceList.clear()

                        }
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

    private fun addMarker(googlePlaceModel: GooglePlaceModel, position: Int) {
        val markerOptions = MarkerOptions()
            //get the position according to the model we passed as argument
            .position(
                LatLng(
                    googlePlaceModel.geometry?.location?.lat!!,
                    googlePlaceModel.geometry?.location?.lng!!
                )
            )
            .title(googlePlaceModel.name)
            .snippet(googlePlaceModel.vicinity)

        markerOptions.icon(getCustomIcon())
        //add the marker
        mGoogleMap?.addMarker(markerOptions)?.tag = position
        // TODO: 5/12/21 add marker to googleMap

    }

    //create the custom icon for the marker
    private fun getCustomIcon(): BitmapDescriptor? {
        //define the icon backgroud. ContextCompat ---> helps to accessing features in Context so
        // that you have to pass the context of the activity.
        val backGround = ContextCompat.getDrawable(requireContext(), R.drawable.ic_location)
        //change color
        backGround?.setTint(resources.getColor(R.color.quantum_googred900, null))
        backGround?.setBounds(0, 0, backGround.intrinsicWidth, backGround.intrinsicHeight)
        //create the icon image
        val bitmap = Bitmap.createBitmap(
            backGround?.intrinsicWidth!!, backGround.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        //get a canvas object
        val canvas = Canvas(bitmap)
        //draw canvas
        backGround.draw(canvas)
        return BitmapDescriptorFactory.fromBitmap(bitmap)


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

    fun stopLocationUpdates() {
        fusedLocationProviderClient?.removeLocationUpdates(locationCallback)

    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    override fun onResume() {
        super.onResume()
        if (fusedLocationProviderClient != null) {
            startLocationUpdates()
            //remove the current marker becasue it could have been changed.
            currentMarker?.remove()
        }
    }

    private fun setUpRecyclerView() {
        //SnapHelper used to set the recycler to the next position directly.
        val snapHelper: SnapHelper = PagerSnapHelper()
        //init the adapter
        googleplaceAdapter = GooglePlaceAdapter(this) //interface from constructor

        binding.placesRecyclerView.apply {
            layoutManager =
                LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
            setHasFixedSize(false)
            //define the recycler adapter.
            adapter = googleplaceAdapter

            addOnScrollListener(object : RecyclerView.OnScrollListener(){
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    super.onScrolled(recyclerView, dx, dy)
                    //define the layout of the recycler
                    val linearManager = recyclerView.layoutManager as LinearLayoutManager
                    //get the position of the first full visible item from recycler
                    val position = linearManager.findFirstCompletelyVisibleItemPosition()
                    //if we have a position, means that we have a full element visible
                    if (position > -1){
                        //get the full model from the googlePlaceList (this is filled when we press the
                        // a chip with all places nearby)
                        val googlePlaceModel: GooglePlaceModel = googlePlaceList[position]
                        mGoogleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    googlePlaceModel.geometry?.location?.lat!!,
                                    googlePlaceModel.geometry?.location?.lng!!,

                                ), 20f
                            )
                        )

                    }
                }

            })

        }
        //attach the snaphelper to the recyclerview
        snapHelper.attachToRecyclerView(binding.placesRecyclerView)


    }


    override fun onMarkerClick(marker: Marker): Boolean {
        //cogemos el tag del marker.
        val markerTag = marker.tag as Int
        Log.d("TAG", "onMarkerClick: $markerTag")
        //y hacemos un scroll del recycler view hasta ese marker tag.
        //con esto vamos a sincronizar el scroll con la posicon del marquer.
        binding.placesRecyclerView.scrollToPosition(markerTag)
        return false

    }

    override fun onSaveClick(googlePlaceModel: GooglePlaceModel) {
        if(userSavedLocationId.contains(googlePlaceModel.placeId)){
            AlertDialog.Builder(requireContext())
                .setTitle("Remove Place")
                .setMessage("Are you sure to remove this place?")
                .setPositiveButton("Yes") { _, _ ->
                    removePlace(googlePlaceModel)
                }
                .setNegativeButton("No"){_,_ ->}
                .create().show()
        }else{

            addPlace(googlePlaceModel)
        }



    }




    private fun removePlace(googlePlaceModel: GooglePlaceModel) {
        //remove from userSavedLocationId
        userSavedLocationId.remove(googlePlaceModel.placeId)
        //get the position of that googlePlaceModel clicked on googleplaceList (that has all of the
        //clicked chip places.)
        val index = googlePlaceList.indexOf(googlePlaceModel)
        //set to false "saved" property
        googlePlaceList[index].saved = false
        //notify the adapter to get again the data and not show the filled icon of saved place.
        googleplaceAdapter.notifyDataSetChanged()

        Snackbar.make(binding.root, "Place removed", Snackbar.LENGTH_SHORT)
            .setAction("Undo"){
                userSavedLocationId.add(googlePlaceModel.placeId!!)
                googlePlaceList[index].saved = true
                googleplaceAdapter.notifyDataSetChanged()

            }
            //listener for transient snackbar, when this one hides.
            .addCallback(object : BaseTransientBottomBar.BaseCallback<Snackbar?>(){
                override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                    super.onDismissed(transientBottomBar, event)
                    //remove place from firebase of the current user logged.
                    lifecycleScope.launchWhenStarted {
                        locationViewModel.removePlace(userSavedLocationId).collect {
                            when(it){
                                is State.Loading -> {
                                    if(it.flag == true){
                                        loadingDialog.startLoading()
                                    }

                                }
                                is State.Succes -> {
                                    loadingDialog.stopLoading()
                                    Snackbar.make(
                                        binding.root,
                                        it.data.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                }
                                is State.Failed -> {
                                    loadingDialog.stopLoading()
                                    Snackbar.make(
                                        binding.root,
                                        it.error.toString(),
                                        Snackbar.LENGTH_SHORT
                                    ).show()

                                }
                            }
                        }
                    }

                }
            } )
            .show()




    }

    private fun addPlace(googlePlaceModel: GooglePlaceModel) {
        lifecycleScope.launchWhenStarted {
            locationViewModel.addUserPlace(googlePlaceModel, userSavedLocationId).collect {
                when(it){
                    is State.Loading ->{
                        if(it.flag == true){
                            loadingDialog.startLoading()
                        }
                    }
                    is State.Succes -> {
                        loadingDialog.stopLoading()
                        val placeModel: GooglePlaceModel = it.data as GooglePlaceModel
                        //userSavedLocationId.add(placeModel.placeId!!)
                        val index = googlePlaceList.indexOf(placeModel)
                        googlePlaceList[index].saved = true
                        googleplaceAdapter.notifyDataSetChanged()
                        Snackbar.make(binding.root, "Saved Successfully", Snackbar.LENGTH_SHORT)
                            .show()
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


    override fun onDirectionClick(googlePlaceModel: GooglePlaceModel) {


    }
}