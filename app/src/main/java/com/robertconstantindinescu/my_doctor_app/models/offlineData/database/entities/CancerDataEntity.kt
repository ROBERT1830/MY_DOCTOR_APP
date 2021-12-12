package com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.CANCER_DATA_TABLE

@Entity(tableName = CANCER_DATA_TABLE)
data class CancerDataEntity(
    @ColumnInfo(name = "date")
    var date: String = "",
    @ColumnInfo(name = "img")
    var cancerImg: Bitmap? = null,
    @ColumnInfo(name = "result")
    var outcome: String = ""

//
//    @ColumnInfo(name = "statistics")
//    var statistics: String = "",
//    @ColumnInfo(name = "probability")
//    var probability: String = ""



) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


}