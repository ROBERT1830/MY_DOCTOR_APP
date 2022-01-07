package com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

import com.robertconstantindinescu.my_doctor_app.databinding.PlaceItemLayoutBinding
import com.robertconstantindinescu.my_doctor_app.interfaces.NearLocationInterface
import com.robertconstantindinescu.my_doctor_app.interfaces.SavedPlaceInterface
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel
import com.robertconstantindinescu.my_doctor_app.models.placesModel.SavedPlaceModel

class SavedPlacesAdapter(private val nearLocationInterface: NearLocationInterface) : RecyclerView.Adapter<SavedPlacesAdapter.MyViewHolder>() {

    private var googlePlacesList = emptyList<GooglePlaceModel>()

    fun setUpAdapter(googlePlacesList: ArrayList<GooglePlaceModel>){
        this.googlePlacesList = googlePlacesList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecord = this.googlePlacesList[position]
        holder.binding.listener = nearLocationInterface
        holder.bind(currentRecord)
    }

    override fun getItemCount(): Int {
        return this.googlePlacesList.size
    }

    class MyViewHolder(val binding: PlaceItemLayoutBinding) :
        RecyclerView.ViewHolder(binding.root){
            fun bind(googlePlaceModel: GooglePlaceModel){
                binding.googlePlaceModel = googlePlaceModel
                binding.executePendingBindings()
            }

            companion object{
                fun from(parent: ViewGroup): MyViewHolder{
                    val layoutInflater = LayoutInflater.from(parent.context)
                    val binding = PlaceItemLayoutBinding.inflate(layoutInflater, parent, false)
                    return MyViewHolder(binding)
                }
            }

        }

}