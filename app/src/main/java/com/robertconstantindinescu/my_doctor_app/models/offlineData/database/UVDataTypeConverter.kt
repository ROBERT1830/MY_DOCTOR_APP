package com.robertconstantindinescu.my_doctor_app.models.offlineData.database

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.robertconstantindinescu.my_doctor_app.models.onlineData.radiationIndex.UVResponse

class UVDataTypeConverter {

    var gson = Gson()

    @TypeConverter
    fun uvResponseToString(uvResponse: UVResponse): String{
        return  gson.toJson((uvResponse))
    }

    @TypeConverter
    fun stringToUvResponser(data:String): UVResponse {
        val listTyp = object : TypeToken<UVResponse>() {}.type
        return gson.fromJson(data, listTyp)
    }
}