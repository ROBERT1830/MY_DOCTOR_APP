package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters.RequestAppointmentAdapter
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity

class RequestAppointmentBinding {
    companion object{

        @BindingAdapter("viewVisibility2", "setData2", requireAll = false)
        @JvmStatic
        fun setDataCancer(
            view: View,
            canceDataEntity: List<CancerDataEntity>?,
            mapAdapter: RequestAppointmentAdapter?
        ){


            if (canceDataEntity.isNullOrEmpty()){
                when(view){
                    is ImageView -> {view.visibility = View.VISIBLE}
                    is TextView -> {view.visibility = View.VISIBLE}
                    is RecyclerView -> {view.visibility = View.INVISIBLE}
                }

            }else{
                when(view){
                    is ImageView -> {view.visibility = View.INVISIBLE}
                    is TextView -> {view.visibility = View.INVISIBLE}
                    is RecyclerView -> {
                        view.visibility = View.VISIBLE
                        mapAdapter?.setData(canceDataEntity)
                    }
                }
            }
        }
        @BindingAdapter("setDate")
        @JvmStatic
        fun setDate(textView: TextView, date: String){
            textView.text = date.toString()
        }
    }
}