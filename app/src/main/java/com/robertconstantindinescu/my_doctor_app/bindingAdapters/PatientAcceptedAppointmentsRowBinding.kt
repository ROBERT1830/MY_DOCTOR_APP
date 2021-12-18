package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.widget.Button
import androidx.databinding.BindingAdapter

class PatientAcceptedAppointmentsRowBinding {
    companion object{

        @BindingAdapter("setButtonText")
        @JvmStatic
        fun setButtonText(button: Button, appointmentStatus: String){
            if (appointmentStatus.contains("accepted")){
                button.text = "CANCEL"
            }else button.text = "DELETE"
        }

    }
}