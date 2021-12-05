package com.robertconstantindinescu.my_doctor_app.interfaces

import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel

interface NearLocationInterface {
    fun onSaveClick(googlePlaceModel: GooglePlaceModel)

    fun onDirectionClick(googlePlaceModel: GooglePlaceModel)


}