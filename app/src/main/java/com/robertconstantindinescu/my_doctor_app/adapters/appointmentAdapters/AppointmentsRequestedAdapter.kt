package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAppointmentsRequestsRowBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.PendingDoctorAppointmentRequestsInterface
import com.robertconstantindinescu.my_doctor_app.models.appointmentModels.PendingDoctorAppointmentModel
import com.robertconstantindinescu.my_doctor_app.utils.CancerDiffUtil

class AppointmentsRequestedAdapter(private val pendingDoctorAppointmentRequestInterface: PendingDoctorAppointmentRequestsInterface) :
    RecyclerView.Adapter<AppointmentsRequestedAdapter.MyViewHolder>() {

    private var requestedDoctorAppointments = ArrayList<PendingDoctorAppointmentModel>()

    fun setUpAdapter(requestedDoctorAppointments: ArrayList<PendingDoctorAppointmentModel>) {
        val dataDiffUtil = CancerDiffUtil(this.requestedDoctorAppointments, requestedDoctorAppointments)
        val diffUtilResult = DiffUtil.calculateDiff(dataDiffUtil)
        this.requestedDoctorAppointments = requestedDoctorAppointments
        // TODO: 12/12/21 dif util
        diffUtilResult.dispatchUpdatesTo(this)
        notifyDataSetChanged()
    }

     fun delete(pendingAppointmentDoctorModel: PendingDoctorAppointmentModel) {
        requestedDoctorAppointments.remove(pendingAppointmentDoctorModel)
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        val currentRecord = this.requestedDoctorAppointments[position]
        holder.binding.listener = pendingDoctorAppointmentRequestInterface
        holder.binding.position = position
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