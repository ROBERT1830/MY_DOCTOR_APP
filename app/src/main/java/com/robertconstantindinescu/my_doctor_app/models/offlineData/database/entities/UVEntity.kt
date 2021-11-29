package com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex.UVResponse
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RADIATION_WEATHER_TABLE

@Entity(tableName = RADIATION_WEATHER_TABLE)
class UVEntity (var UvResponse: UVResponse) {
    @PrimaryKey(autoGenerate = false)
    var id: Int = 0
}