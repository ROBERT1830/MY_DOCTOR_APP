package com.robertconstantindinescu.my_doctor_app.models.offlineData.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.robertconstantindinescu.my_doctor_app.models.RecipesTypeConverter
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.RecipesEntity
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.UVEntity

// TODO: 15/11/21 SPECIFY THE DATABASE TYPECONVERTER 

@Database(
    entities = [CancerDataEntity::class, UVEntity::class, RecipesEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(
    UserCancerDataTypeConverter::class,
    UVDataTypeConverter::class,
    RecipesTypeConverter::class
)
abstract class UserCancerDatabase : RoomDatabase() {
    abstract fun cancerDataDao(): DataDao

}