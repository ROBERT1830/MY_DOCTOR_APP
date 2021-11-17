package com.robertconstantindinescu.my_doctor_app.ui.fragments

import android.Manifest
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentRadiationBinding
import com.vmadalin.easypermissions.EasyPermissions
import com.vmadalin.easypermissions.dialogs.SettingsDialog
import dagger.hilt.android.AndroidEntryPoint

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
//private const val ARG_PARAM1 = "param1"
//private const val ARG_PARAM2 = "param2"

class RadiationFragment : Fragment(), EasyPermissions.PermissionCallbacks {
//    // TODO: Rename and change types of parameters
//    private var param1: String? = null
//    private var param2: String? = null
    companion object {
        const val PERMISSION_LOCATION_REQUEST_CODE = 1
    }

    private var _binding: FragmentRadiationBinding? = null
    private val binding get() = _binding!!


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
//            param1 = it.getString(ARG_PARAM1)
//            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRadiationBinding.inflate(inflater, container, false)
        //return inflater.inflate(R.layout.fragment_radiation, container, false)

        /**Every time the fragment is launched we have to check for the permisisons.
         * If we have permissions we want to show the data and if the app doesnt have
         * the permission then we will show up the button.*/
        setViewVisibility()
        binding.btnGrantPermission.setOnClickListener {
            requestLocationPermission()
        }


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
    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
        // TODO: 17/11/21  Aqui es donde vamos a mostrar todos los datos de la appi.
        Toast.makeText(
            requireContext(),
            "Permission Granted!",
            Toast.LENGTH_SHORT
        ).show()
        setViewVisibility()
    }

    /**
     * Function that will change the visibility of the button.
     * First we check if the app has the permission to the location, because the user provided it
     * if the user has granted the permissions then button will not appear
     * On the contrary if the user doesn't grant the permission the button will
     * be available just to allow the user grant the permisison.
     */
    private fun setViewVisibility() {
        if (hasLocationPermission()) {
            binding.txtPermissionGranted.visibility  = View.VISIBLE
            binding.btnGrantPermission.visibility = View.GONE
        } else {
            binding.txtPermissionGranted.visibility  = View.GONE
            binding.btnGrantPermission.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}








//    companion object {
//        /**
//         * Use this factory method to create a new instance of
//         * this fragment using the provided parameters.
//         *
//         * @param param1 Parameter 1.
//         * @param param2 Parameter 2.
//         * @return A new instance of fragment RadiationFragment.
//         */
//        // TODO: Rename and change types and number of parameters
//        @JvmStatic
//        fun newInstance(param1: String, param2: String) =
//            RadiationFragment().apply {
//                arguments = Bundle().apply {
//                    putString(ARG_PARAM1, param1)
//                    putString(ARG_PARAM2, param2)
//                }
//            }
//    }
