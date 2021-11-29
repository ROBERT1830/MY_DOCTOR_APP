package com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.view.LayoutInflater
import android.view.View
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.robertconstantindinescu.my_doctor_app.databinding.InfoWindowLayoutBinding
import kotlin.math.roundToInt
import com.google.maps.android.SphericalUtil

/**
 * This class will be in char of drawing a custom marker info.
 */
class InfoWindowAdapter(private val location: Location, context: Context) :
    GoogleMap.InfoWindowAdapter {
        //binding using the parend from context. We have to get the inflater from parent so that
        //this can inflate the xml layout.
        private val binding: InfoWindowLayoutBinding = InfoWindowLayoutBinding.inflate(
            LayoutInflater.from(context), null, false
        )

    /**
     * Each time a marker goes clicked this method will be triggered.
     */
    @SuppressLint("SetTextI18n")
    override fun getInfoWindow(marker: Marker): View { //marker es un icono en un sitio del mapa
        binding.txtLocationName.text = marker.title
        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(
                location.latitude, location.longitude
            ), marker.position
        )
        if (distance.roundToInt() > 1000) {
            val kilometers = (distance / 1000).roundToInt()
            binding.txtLocationDistance.text = "$kilometers KM"
        } else {
            binding.txtLocationDistance.text = "${distance.roundToInt()} Meters"
        }
        val speed = location.speed
        if (speed.roundToInt() > 0) {
            val time = distance / speed
            binding.txtLocationTime.text = "${time.roundToInt()} sec"
        } else
            binding.txtLocationTime.text = "N/A"

        return binding.root
    }

    @SuppressLint("SetTextI18n")
    override fun getInfoContents(marker: Marker): View {
        binding.txtLocationName.text = marker.title
        val distance = SphericalUtil.computeDistanceBetween(
            LatLng(
                location.latitude, location.longitude
            ), marker.position
        )
        if (distance.roundToInt() > 1000) {
            val kilometers = (distance / 1000).roundToInt()
            binding.txtLocationDistance.text = "$kilometers KM"
        } else {
            binding.txtLocationDistance.text = "${distance.roundToInt()} Meters"
        }
        val speed = location.speed
        if (speed.roundToInt() > 0) {
            val time = distance / speed
            binding.txtLocationTime.text = "${time.roundToInt()} sec"
        } else
            binding.txtLocationTime.text = "N/A"

        return binding.root
    }
}