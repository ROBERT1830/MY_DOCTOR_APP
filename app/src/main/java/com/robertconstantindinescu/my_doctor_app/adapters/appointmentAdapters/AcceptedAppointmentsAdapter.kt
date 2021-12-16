package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAceptedApointmentsRowBinding
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsRowBinding
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.AcceptedDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import dagger.hilt.android.AndroidEntryPoint


class AcceptedAppointmentsAdapter: RecyclerView.Adapter<AcceptedAppointmentsAdapter.MyViewHolder>() {

    private var acceptedDoctorAppointments = emptyList<AcceptedDoctorAppointmentModel>()
    fun setUpAdapter(acceptedDoctorAppointments: ArrayList<AcceptedDoctorAppointmentModel>) {
        this.acceptedDoctorAppointments = acceptedDoctorAppointments
        // TODO: 12/12/21 dif util
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AcceptedAppointmentsAdapter.MyViewHolder {

        return AcceptedAppointmentsAdapter.MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: AcceptedAppointmentsAdapter.MyViewHolder, position: Int) {

        val currentRecord = this.acceptedDoctorAppointments[position]
        //holder.binding.listener = pendingDoctorAppointmentRequestInterface
        holder.bind(currentRecord)
    }

    override fun getItemCount(): Int {
        return this.acceptedDoctorAppointments.size
    }

    class MyViewHolder(val binding: FragmentAceptedApointmentsRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(acceptedDoctorAppointmentModel: AcceptedDoctorAppointmentModel) {
            binding.acceptedDoctorAppointmentModel = acceptedDoctorAppointmentModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    FragmentAceptedApointmentsRowBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }
}