package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.annotation.SuppressLint
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.res.TypedArrayUtils.getString
import androidx.core.content.res.TypedArrayUtils.getText
import androidx.databinding.BindingAdapter
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.ui.fragments.RadiationFragment.Companion.countryName
import com.robertconstantindinescu.my_doctor_app.ui.fragments.RadiationFragment.Companion.localityName
import java.text.SimpleDateFormat
import java.util.*

class RadiationBindingAdapter {

    companion object{


        @BindingAdapter("setTextInfoRadiation")
        @JvmStatic
        fun setTextInfoRadiation(textView: TextView, uvIndex: Double){
            when{
                uvIndex in 1.0..2.0 -> { textView.resources.getText(R.string.low_risk) }
                uvIndex in 3.0..5.0 -> { textView.resources.getText(R.string.moderate_risk) }
                uvIndex in 6.0..7.0 -> { textView.resources.getText(R.string.high_risk) }
                uvIndex in 8.0..10.0 -> { textView.resources.getText(R.string.veryHigh_risk) }
                uvIndex >= 11.0 -> { textView.resources.getText(R.string.extreme_risk) }
            }
        }

        @BindingAdapter("setIndexRadiatio")
        @JvmStatic
        fun setIndexRadiatio(textView: TextView, uvIndex: Double){
            textView.text = uvIndex.toString()
        }

        @BindingAdapter("setImageViewRadiationInfo")
        @JvmStatic
        fun setImageViewRadiationInfo(imageView: ImageView, uvIndex: Double){
            when{
                uvIndex in 1.0..2.0 -> { imageView.setImageResource(R.drawable.img_riesgo_bajo) }
                uvIndex in 3.0..5.0 -> { imageView.setImageResource(R.drawable.img_riesgo_moderado) }
                uvIndex in 6.0..7.0 -> { imageView.setImageResource(R.drawable.img_riesgo_alato) }
                uvIndex in 8.0..10.0 -> { imageView.setImageResource(R.drawable.img_riesgo_muy_alto) }
                uvIndex >= 11.0 -> { imageView.setImageResource(R.drawable.img_extremo) }
            }
        }

        @BindingAdapter("setInfoRadiationBody")
        @JvmStatic
        fun setInfoRadiationBody(textView: TextView,uvIndex: Double ){
            when{
                uvIndex in 1.0..2.0 -> { textView.resources.getText(R.string.low_risk) }
                uvIndex in 3.0..5.0 -> { textView.resources.getText(R.string.moderate_risk) }
                uvIndex in 6.0..7.0 -> { textView.resources.getText(R.string.high_risk) }
                uvIndex in 8.0..10.0 -> { textView.resources.getText(R.string.veryHigh_risk) }
                uvIndex >= 11.0 -> { textView.resources.getText(R.string.extreme_risk) }
            }
        }


//        @BindingAdapter("setDate")
//        @JvmStatic
//        fun setDate(textView: TextView){
//            val sdf = SimpleDateFormat.getDateTimeInstance()
//            sdf.format(Date())
//            textView.text = sdf.toString()
//        }
//
//        fun setLocation(textView: TextView){
//            textView.text =  "${countryName} ; ${localityName}"
//        }


    }
}