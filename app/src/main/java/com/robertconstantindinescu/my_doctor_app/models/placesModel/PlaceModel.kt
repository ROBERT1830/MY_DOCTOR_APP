package com.robertconstantindinescu.my_doctor_app.models.placesModel

/**
 * Data class for each and every chip from chip group.
 * Using that class provides us a way to generate them in code dinamycally.
 */
data class PlaceModel(
    var id: Int,
    var drawableId: Int,
    var name: String,
    var placeType: String

)


