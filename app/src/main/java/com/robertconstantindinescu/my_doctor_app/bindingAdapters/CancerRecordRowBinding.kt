package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.graphics.Bitmap
import android.widget.ImageView
import android.graphics.BitmapFactory

import android.view.View
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
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

        @BindingAdapter("applyMalignBenignColor")
        @JvmStatic
        fun applyMalignBenignColor(view: View, text: String ){
            if (text.equals("malignant", true)){
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
        @BindingAdapter("sesetProbabilityt")
        @JvmStatic
        fun setProbability(textView: TextView, probability: String){
            textView.text = probability

        }
        @BindingAdapter("setDate")
        @JvmStatic
        fun setDate(textView: TextView, date: Date){
            textView.text = date.toString()
        }

    }
}





























