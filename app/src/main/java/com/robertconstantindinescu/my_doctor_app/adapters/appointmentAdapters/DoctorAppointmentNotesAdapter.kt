package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentDoctorAppointmentNotesRowBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDataFirebaseModel

class DoctorAppointmentNotesAdapter :
    RecyclerView.Adapter<DoctorAppointmentNotesAdapter.MyViewHolder>() {

    private var cancerDataFirebaseModelList = emptyList<CancerDataFirebaseModel>()
    fun setUpAdapter(cancerDataFirebaseModelList: ArrayList<CancerDataFirebaseModel>){
        this.cancerDataFirebaseModelList = cancerDataFirebaseModelList
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentRecord = this.cancerDataFirebaseModelList[position]

        holder.bind(currentRecord, position)
    }

    override fun getItemCount(): Int {
        return cancerDataFirebaseModelList.size
    }

    //here we want to have the binding.position because we will add the note in a particular position
    //in the array. When the doctor writes something this will be added and updated each time in
    //a arrayList. So if he want to go to a previous one, if we dont have the position it will be
    //added at the end of the list and don't want that.
    class MyViewHolder(val binding: FragmentDoctorAppointmentNotesRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(cancerDataFirebaseModel: CancerDataFirebaseModel, position: Int) {
            binding.cancerDataFirebaseModel = cancerDataFirebaseModel
            binding.position = position

            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    FragmentDoctorAppointmentNotesRowBinding.inflate(layoutInflater, parent, false)
                return  MyViewHolder(binding)
            }
        }
    }
}