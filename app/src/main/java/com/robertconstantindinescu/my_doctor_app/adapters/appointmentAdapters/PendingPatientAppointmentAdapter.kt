package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentPendingPatientAppointmentsRowBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingPatientAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel

class PendingPatientAppointmentAdapter :
    RecyclerView.Adapter<PendingPatientAppointmentAdapter.MyViewHolder>() {
    private var pendingPatientAppointmentList = emptyList<PendingPatientAppointmentModel>()


    fun setUpAdapter(pendingPatientAppointmentList: ArrayList<PendingPatientAppointmentModel>) {
        this.pendingPatientAppointmentList = pendingPatientAppointmentList
        // TODO: 8/12/21 use diff util but for now is only a test
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): PendingPatientAppointmentAdapter.MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(
        holder: PendingPatientAppointmentAdapter.MyViewHolder,
        position: Int
    ) {
        val currenRecord = this.pendingPatientAppointmentList[position]
        holder.binding(currenRecord)
    }

    override fun getItemCount(): Int {
        return this.pendingPatientAppointmentList.size
    }

    class MyViewHolder(private val binding: FragmentPendingPatientAppointmentsRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun binding(pendigPatienAppointmentModel: PendingPatientAppointmentModel) {
            binding.patientAppointmentModel = pendigPatienAppointmentModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FragmentPendingPatientAppointmentsRowBinding.inflate(
                    layoutInflater,
                    parent,
                    false
                )
                return MyViewHolder(binding)
            }
        }
    }


}