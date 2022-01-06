package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.DetailAcceptedPatientAppointmentRowBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.CancerDoctorNote

class DetailAcceptedPatientAppointmentAdapter :
    RecyclerView.Adapter<DetailAcceptedPatientAppointmentAdapter.MyViewHolder>() {


    private var cancerDoctorNotes = ArrayList<CancerDoctorNote>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecord = this.cancerDoctorNotes[position]
        holder.bind(currentRecord)
    }

    override fun getItemCount(): Int {
        return cancerDoctorNotes.size
    }



    fun setUpAdapter(cancerDoctorNotes: ArrayList<CancerDoctorNote>){
        this.cancerDoctorNotes = cancerDoctorNotes
        notifyDataSetChanged()
    }

    class MyViewHolder(val binding: DetailAcceptedPatientAppointmentRowBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cancerDoctorNote: CancerDoctorNote) {
            binding.doctorNoteModel = cancerDoctorNote
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = DetailAcceptedPatientAppointmentRowBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return MyViewHolder(binding)
            }
        }
    }
}