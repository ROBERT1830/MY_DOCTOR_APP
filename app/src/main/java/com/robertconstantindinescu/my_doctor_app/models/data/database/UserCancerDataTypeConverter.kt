package com.robertconstantindinescu.my_doctor_app.models.data.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

class UserCancerDataTypeConverter {

    //convert bitmap to byteArray to write in room

    /**
     * Convert bitmap to byteArray, so we can store in room
     * With  @TypeConverter we are telling room whenever he recognse a bitmap object inside
     * the entity class, he shoul automatically convert taht to byteArray.
     */
    @TypeConverter
    fun fromBitmap(bitmap: Bitmap): ByteArray{
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
        return outputStream.toByteArray()
    }

    //convert bytearray to bitmap
    @TypeConverter
    fun toBitmap(byteArray: ByteArray): Bitmap{
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)

    }
}