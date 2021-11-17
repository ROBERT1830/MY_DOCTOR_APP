package com.robertconstantindinescu.my_doctor_app.models.offlineData.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity

// TODO: 15/11/21 SPECIFY THE DATABASE TYPECONVERTER 

@Database(
    entities = [CancerDataEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(UserCancerDataTypeConverter::class)
abstract class UserCancerDatabase: RoomDatabase() {
    abstract fun cancerDataDao(): CancerDataDao

}