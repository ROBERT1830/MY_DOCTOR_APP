package com.robertconstantindinescu.my_doctor_app.models.data.database.entities

import android.graphics.Bitmap
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.sql.Date

@Entity(tableName = "cancer_data_table")
data class CancerDataEntity(
    @ColumnInfo(name = "date")
    var daete: String = "",
    @ColumnInfo(name = "img")
    var img: Bitmap? = null,
    @ColumnInfo(name = "result")
    var result: String = ""

//
//    @ColumnInfo(name = "statistics")
//    var statistics: String = "",
//    @ColumnInfo(name = "probability")
//    var probability: String = ""



) {
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0


}