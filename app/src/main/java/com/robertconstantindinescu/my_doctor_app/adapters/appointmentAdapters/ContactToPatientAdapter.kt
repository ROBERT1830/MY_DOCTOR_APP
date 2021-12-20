package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAvailableDoctorsItemBinding
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentContactToPatientRowBinding
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.PatientModel

class ContactToPatientAdapter: RecyclerView.Adapter<ContactToPatientAdapter.MyViewHolder>() {
    private var patientsToCallList = emptyList<PatientModel>()


    fun setUpAdapter(patientsToCallList: ArrayList<PatientModel>){
        this.patientsToCallList = patientsToCallList
        // TODO: 8/12/21 use diff util but for now is only a test
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentRecord = this.patientsToCallList[position]
        holder.bind(currentRecord)
    }

    override fun getItemCount(): Int {
        return this.patientsToCallList.size
    }

    class MyViewHolder(private val binding: FragmentContactToPatientRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(patientModel: PatientModel) {
            binding.patientModel = patientModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //inflate --> 1)parametrer is the parent in which the child will be inflated.
                val binding =
                    FragmentContactToPatientRowBinding.inflate(layoutInflater, parent, false)
                return  MyViewHolder(binding)
            }
        }

    }
}