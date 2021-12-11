package com.robertconstantindinescu.my_doctor_app.adapters.appointmentAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentAvailableDoctorsItemBinding
import com.robertconstantindinescu.my_doctor_app.models.loginUsrModels.DoctorModel

class AvailableDoctorsAdapter : RecyclerView.Adapter<AvailableDoctorsAdapter.MyViewHolder>() {

    private var availableDoctorsList = emptyList<DoctorModel>()


    fun setUpAdapter(availableDoctorsList: ArrayList<DoctorModel>){
        this.availableDoctorsList = availableDoctorsList
        // TODO: 8/12/21 use diff util but for now is only a test
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentDoctor = this.availableDoctorsList[position]
        holder.bind(currentDoctor)

    }

    override fun getItemCount(): Int {
        return this.availableDoctorsList.size
    }


    class MyViewHolder(private val binding: FragmentAvailableDoctorsItemBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(doctor: DoctorModel) {
            binding.doctorModel = doctor
            binding.executePendingBindings()
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                //inflate --> 1)parametrer is the parent in which the child will be inflated.
                val bindig =
                    FragmentAvailableDoctorsItemBinding.inflate(layoutInflater, parent, false)
                return  MyViewHolder(bindig)
            }
        }

    }

    // TODO: 8/12/21 perform the binding adapter to load the image. the other text views will be filled becasuse in each one we have a reference to the model.
    // TODO: 8/12/21 and perform after we get the doctor, the onclick listners in the binding adapter.


}