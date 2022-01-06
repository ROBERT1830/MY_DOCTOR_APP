package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.graphics.Bitmap
import android.widget.ImageView
import android.graphics.BitmapFactory
import android.net.Uri

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import coil.load
import com.robertconstantindinescu.my_doctor_app.R
import java.lang.Byte.decode
import java.sql.Date
import java.util.*


class CancerRecordRowBinding {

    companion object{

        //cargar bitmap en imageview.
        @BindingAdapter("loadImage")
        @JvmStatic
        fun loadImage(imageView: ImageView, bitmap: Bitmap){
            imageView.setImageBitmap(bitmap)

        }

        @BindingAdapter("loadImageFromFirebase")
        @JvmStatic
        fun loadImageFromFirebase(imageView: ImageView, url: String){
            // TODO: 14/12/21  
            imageView.load(url)
        }

        @BindingAdapter("setDoctorNote")
        @JvmStatic
        fun setDoctorNote(textView: TextView, doctorNote:String){
            textView.text = doctorNote.trim()
        }



        @BindingAdapter("applyMalignBenignColor")
        @JvmStatic
        fun applyMalignBenignColor(view: View, text: String ){
            if (text.contains("malignant", true)){
                when(view){
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.red
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.red
                            )
                        )
                    }
                }
            }else{
                when(view){
                    is TextView -> {
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }
        @BindingAdapter("setProbabilityt")
        @JvmStatic
        fun setProbability(textView: TextView, probability: String){
            textView.text = probability

        }
        @BindingAdapter("setDate")
        @JvmStatic
        fun setDate(textView: TextView, date: String){
            textView.text = date.toString()
        }

    }
}





























