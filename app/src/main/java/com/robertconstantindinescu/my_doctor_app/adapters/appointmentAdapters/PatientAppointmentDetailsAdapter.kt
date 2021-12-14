package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPatientAppointmentDetatailsCancerdataRowBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.DetailsCancerDataInterface
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDataFirebaseModel

class PatientAppointmentDetailsAdapter(private val detailsCancerDataInterface: DetailsCancerDataInterface) :
    RecyclerView.Adapter<PatientAppointmentDetailsAdapter.MyViewHolder>() {

    private var cancerDataFirebaseModelList = emptyList<CancerDataFirebaseModel>()


    fun setUpAdapter(cancerDataFirebaseModel: ArrayList<CancerDataFirebaseModel>){
        this.cancerDataFirebaseModelList = cancerDataFirebaseModel
        // TODO: 14/12/21 use dif util
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecord = this.cancerDataFirebaseModelList[position]
        holder.binding.listener = detailsCancerDataInterface
        holder.bind(currentRecord)
    }

    override fun getItemCount(): Int {
        return this.cancerDataFirebaseModelList.size
    }

    class MyViewHolder(val binding: FragmentPatientAppointmentDetatailsCancerdataRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cancerDataFirebaseModel: CancerDataFirebaseModel) {
            binding.cancerDataFirebaseModel = cancerDataFirebaseModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentPatientAppointmentDetatailsCancerdataRowBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return MyViewHolder(binding)

            }
        }
    }
}