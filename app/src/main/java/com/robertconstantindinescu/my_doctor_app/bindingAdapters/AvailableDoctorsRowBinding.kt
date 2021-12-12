package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.util.Log
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel

import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.AvailableDoctorsFragmentDirections
import java.lang.Exception

class AvailableDoctorsRowBinding {
    companion object {

        @BindingAdapter("onAvailableDoctorClickListener")
        @JvmStatic
        fun onAvailableDoctorClickListener(
            availableDoctorLayout: ConstraintLayout,
            doctorModel: DoctorModel
        ){
            availableDoctorLayout.setOnClickListener {
                try {
                    val action= AvailableDoctorsFragmentDirections.actionBtnShowAvailableDoctorsToRequestAppointmentActivity(doctorModel)
                    availableDoctorLayout.findNavController().navigate(action)
                }catch (e: Exception){
                    Log.d("onAvailableDoctorClickListener", e.toString())
                }
            }
        }
    }
}