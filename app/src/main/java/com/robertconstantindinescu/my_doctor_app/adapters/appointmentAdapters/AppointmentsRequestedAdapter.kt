package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsRowBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel

class AppointmentsRequestedAdapter(private val pendingDoctorAppointmentRequestInterface: PendingDoctorAppointmentRequestsInterface) :
    RecyclerView.Adapter<AppointmentsRequestedAdapter.MyViewHolder>() {

    private var requestedDoctorAppointments = emptyList<PendingDoctorAppointmentModel>()

    fun setUpAdapter(requestedDoctorAppointments: ArrayList<PendingDoctorAppointmentModel>) {
        this.requestedDoctorAppointments = requestedDoctorAppointments
        // TODO: 12/12/21 dif util
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentRecord = this.requestedDoctorAppointments[position]
        holder.binding.listener = pendingDoctorAppointmentRequestInterface
        holder.bind(currentRecord)
    }

    override fun getItemCount(): Int {
        return this.requestedDoctorAppointments.size
    }

    class MyViewHolder(val binding: FragmentAppointmentsRequestsRowBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(pendingDoctorAppointmentModel: PendingDoctorAppointmentModel) {
            binding.doctorAppointmentModel = pendingDoctorAppointmentModel
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding =
                    FragmentAppointmentsRequestsRowBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }
    }


}