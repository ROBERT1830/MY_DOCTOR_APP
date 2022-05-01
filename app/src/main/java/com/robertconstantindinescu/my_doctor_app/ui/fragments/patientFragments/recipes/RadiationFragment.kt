package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentRadiationBinding
import com.robertconstantindinescu.my_doctor_app.utils.NetworkListener
import com.robertconstantindinescu.my_doctor_app.utils.NetworkResult
import com.robertconstantindinescu.my_doctor_app.viewmodels.MainViewModel
import com.robertconstantindinescu.my_doctor_app.viewmodels.QueryViewModel
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesMainViewModel
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesQueryUtilsViewModel
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.fragment_radiation.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class RadiationFragment : Fragment(), EasyPermissions.PermissionCallbacks {

    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
        var latitude: Double = 0.0
        var longitude: Double = 0.0
        var countryName: String = ""
        var localityName: String = ""
    }


    private var _binding: FragmentRadiationBinding? = null
    private val binding get() = _binding!!

    private lateinit var mainViewModel: MainViewModel
    private lateinit var queryViewModel: QueryViewModel

    private lateinit var recipesQueryUtilsViewModel: RecipesQueryUtilsViewModel
    private lateinit var networkListener: NetworkListener

    /******** Get location coordinate ********/
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient





    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }

        mainViewModel = ViewModelProvider(requireActivity()).get(MainViewModel::class.java)
        queryViewModel = ViewModelProvider(requireActivity()).get((QueryViewModel::class.java))
        recipesQueryUtilsViewModel =
            ViewModelProvider(requireActivity()).get(RecipesQueryUtilsViewModel::class.java)
    }

    /**This annotations is used becase when we use fusedLocationProviderClient.lastLocation
     * this must have granted permissions. So becauze of that we use that*/
    @RequiresApi(Build.VERSION_CODES.M)
    @SuppressLint("MissingPermission")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRadiationBinding.inflate(inflater, container, false)
        /**Every time the fragment is launched we have to check for the permisisons.
         * If we have permissions we want to show the data and if the app doesnt have
         * the permission then we will show up the button.*/


        recipesQueryUtilsViewModel.readBackOnline.observe(viewLifecycleOwner, Observer {
            recipesQueryUtilsViewModel.backOnline = it
        })
        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->

                Log.d("NetworkListener", status.toString())
                recipesQueryUtilsViewModel.networkStatus = status
                recipesQueryUtilsViewModel.showNetworkStatus()

                if(recipesQueryUtilsViewModel.networkStatus){
                    fusedLocationProviderClient =
                        LocationServices.getFusedLocationProviderClient(requireContext())
                    //if we dont have the permission when we reach the fragment, then we will request one in else

                    if (hasLocationPermission()) {
                        /*lastLocation is like a get, that returns the last locattion currently
                        * available. if has location, return a Location object to work with*/
                        fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                            //if we have a location, then the lambda is activated. And with this
                            //location object we can retrive information from him such as latitude and lingitude
                            /*Moreover, with geoCoder, we can get the city name from those coordinates*/
                            val geoCoder = Geocoder(requireContext())
                            /*with that geoCoder object now we are available to get specific data from
                            * those coordinates. */
                            val currentLocation = geoCoder.getFromLocation(
                                location.latitude,
                                location.longitude,
                                1
                            )
                            latitude = location.latitude
                            longitude = location.longitude
                            countryName = currentLocation.first().countryName
                            localityName = currentLocation.first().locality
                            requestApiData()

                        }
                    } else {
                        requestLocationPermission()


                    }

                }else{
                    recipesQueryUtilsViewModel.showNetworkStatus()
                }


            }
        }




        // TODO: 19/11/21 readback online + networkStatus (NetworkListener) 
        
        return binding.root
    }

    /**
     * This funciton will check if the permision is granted
     * Función que devulve un bool. Usamos la libreria EasyPermissions
     * para comprobar el estado de los permisos.
     * como parametro pasamos el contexto y el permiso que quremos comprobar.
     * This function will return true if this permission is granted by the app and
     * return fals eif there is no granted the permision.
     */
    private fun hasLocationPermission() =
        EasyPermissions.hasPermissions(
            requireContext(),
            Manifest.permission.ACCESS_FINE_LOCATION
        )

    /**
     * Function to request the location permission.and the funciton above will check
     * if the app has permisiosn or not.
     * as a parameters we have the host fragment,
     * the raionale message that is show to the user when the user deny the permission.
     * Next is the request code.
     * Finally we have to pass the permission.
     */
    private fun requestLocationPermission() {
        EasyPermissions.requestPermissions(
            this,
            "This application cannot work without Location Permission.",
            PERMISSION_LOCATION_REQUEST_CODE,
            Manifest.permission.ACCESS_FINE_LOCATION
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        //super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }


    /**
     * Becasuse we implmented the EasyPermissions.PermissionCallbacks
     * this funtion wiil be called when the usr deny the permision.
     * perms.first() is the permission we actually have requested. If the user permanetly denied
     * the permission we should give him a dialog to manually enable that permission.
     * If the permisison has not been denieed permanetly que will request again the permission
     */
    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
        if (EasyPermissions.somePermissionPermanentlyDenied(this, perms)) {
            SettingsDialog.Builder(requireActivity()).build().show()
        } else {
            requestLocationPermission()
        }
    }

    /**
     * Becasuse we implmented the EasyPermissions.PermissionCallbacks
     * this funtion wiil be called when the usr accept/grant the permision.
     */
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // TODO: 17/11/21  Aqui es donde vamos a mostrar todos los datos de la appi.
        Toast.makeText(
            requireContext(),
            "Permission Granted!",
            Toast.LENGTH_SHORT
        ).show()

            if (ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    requireContext(),
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationProviderClient.lastLocation.addOnSuccessListener { location ->
                    //if we have a location, then the lambda is activated. And with this
                    //location object we can retrive information from him such as latitude and lingitude
                    /*Moreover, with geoCoder, we can get the city name from those coordinates*/
                    val geoCoder = Geocoder(requireContext())
                    /*with that geoCoder object now we are available to get specific data from
                    * those coordinates. */
                    val currentLocation = geoCoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )
                    latitude = location.latitude
                    longitude = location.longitude
                    countryName = currentLocation.first().countryName
                    localityName = currentLocation.first().locality

//                var countryCode = currentLocation.first().countryName
//                Log.d("RadiationFragment", countryCode)
//                var subLocality: String = currentLocation.first().locality
//                Log.d("RadiationFragment", subLocality)
//                Log.d("RadiationFragment", location.latitude.toString())
//                Log.d("RadiationFragment", location.longitude.toString())
                }
                return
            }




        requestApiData()
        //setViewVisibility()
    }

    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.M)
    private fun requestApiData() {
        //hacemos la petición
        mainViewModel.getRadiationWeatherData(queryViewModel.applyRadiationQuery())
        //observar la respues
        mainViewModel.radiationWeatherDataResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response){

                is NetworkResult.Success -> {
                    binding.progressBar.visibility = View.GONE
                    var uvIndex: Double = 0.0
                    response.data?.let {
                        uvIndex = it.current.uvi
                    }
                    Log.d("uvIndex", uvIndex.toString())

                    //setUvHeaderInfo(4.7)
                    //setUvImageInfo(4.7)
                    setUvAdviceBody(uvIndex)
                    setLocationDateInfo()
                }
                is NetworkResult.Error -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(
                        requireContext(), response.message.toString(),
                        Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                }

            }
        })

    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun setLocationDateInfo() {
        with(_binding){
            txtView_location_title.visibility = View.VISIBLE
            linear_layout_date_location_info.visibility = View.VISIBLE
        }

        _binding!!.txtViewDate.text = getCurrentDateTime()
        _binding!!.txtViewLocation.text = getCurrentCountryLocality()
    }

    private fun setUvAdviceBody(uvIndex: Double) {
        txtView_advice_title.visibility = View.VISIBLE
        txtView_advice_body.visibility = View.VISIBLE
        imgView_info.visibility = View.VISIBLE
        relative_layout_resume.visibility = View.VISIBLE
        _binding!!.txtUvIndexResult.text = uvIndex.toString()
        when(uvIndex){
             in 0.0..2.0 -> {
                 _binding!!.txtViewAdviceBody.text = getString(R.string.low_risk_body)
                 _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_bajo)
                 _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.low_risk)
             }
             in 2.0..5.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.moderate_risk_body)
                 _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_moderado)
                 binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.moderate_risk)
             }
             in 5.0..7.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.high_risk_body)
                 _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_alato)
                 _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.high_risk)
            }
             in 7.0..10.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.veryHigh_risk_body)
                 _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_muy_alto)
                 _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.veryHigh_risk)
            }
            in 10.0..15.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.extreme_risk_body)
                _binding!!.imgViewInfo.setImageResource(R.drawable.img_extremo)
                _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.extreme_risk)
            }
        }
    /*    when{
            uvIndex >= 0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.low_risk_body)
            }
            uvIndex >= 3.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.moderate_risk_body)
            }
            uvIndex >= 6.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.high_risk_body)
            }
            uvIndex >= 8.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.veryHigh_risk_body)
            }
            uvIndex >= 11.0 -> {
                _binding!!.txtViewAdviceBody.text = getString(R.string.extreme_risk_body)
            }
        }*/
    }

//    private fun setUvImageInfo(uvIndex: Double) {
//        imgView_info.visibility = View.VISIBLE
//        when{
//            uvIndex >= 0  -> { _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_bajo) }
//            uvIndex >= 3.0 -> { _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_moderado) }
//            uvIndex >= 6.0 -> {
//                _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_alato)
//            }
//            uvIndex >= 8.0 -> {
//                _binding!!.imgViewInfo.setImageResource(R.drawable.img_riesgo_muy_alto)
//            }
//            uvIndex >= 11.0 -> {
//                _binding!!.imgViewInfo.setImageResource(R.drawable.img_extremo)
//            }
//        }
//    }

//    private fun setUvHeaderInfo(uvIndex: Double) {
//        relative_layout_resume.visibility = View.VISIBLE
//        when{
//            uvIndex >= 0  -> { _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.low_risk) }
//            uvIndex >= 3.0 -> { binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.moderate_risk) }
//            uvIndex >= 6.0 -> {
//                _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.high_risk)
//            }
//            uvIndex >= 8.0 -> {
//                _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.veryHigh_risk)
//            }
//            uvIndex >= 11.0 -> {
//                _binding!!.txtViewTxtInfoUvIndex.text = getString(R.string.extreme_risk)
//            }
//
//        }
//        _binding!!.txtUvIndexResult.text = uvIndex.toString()
//
//    }

    private fun getCurrentCountryLocality(): String? {

        var countryLocality = "$countryName | $localityName"
        return countryLocality
    }

    @SuppressLint("SimpleDateFormat")
    @RequiresApi(Build.VERSION_CODES.O)
    private fun getCurrentDateTime(): String? {

        //val sdf = SimpleDateFormat("yyy/MM/dd HH:mm_ss")
        val sdf = SimpleDateFormat("EEE, d MMM yyy HH:mm:ss")
        val cal = Calendar.getInstance()
        return sdf.format(cal.time).toString()

    }

    /**
     * Function that will change the visibility of the button.
     * First we check if the app has the permission to the location, because the user provided it
     * if the user has granted the permissions then button will not appear
     * On the contrary if the user doesn't grant the permission the button will
     * be available just to allow the user grant the permisison.
     */
//    private fun setViewVisibility() {
//        if (hasLocationPermission()) {
//            binding.txtPermissionGranted.visibility  = View.VISIBLE
//            binding.btnGrantPermission.visibility = View.GONE
//        } else {
//            binding.txtPermissionGranted.visibility  = View.GONE
//            binding.btnGrantPermission.visibility = View.VISIBLE
//        }
//    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}


