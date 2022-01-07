package com.robertconstantindinescu.my_doctor_app.interfaces

import com.robertconstantindinescu.my_doctor_app.models.placesModel.SavedPlaceModel

interface SavedPlaceInterface {
    fun onDirectionClick(savedPlaceModel: SavedPlaceModel)
}