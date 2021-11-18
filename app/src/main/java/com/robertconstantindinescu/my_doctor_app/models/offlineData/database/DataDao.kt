package com.robertconstantindinescu.my_doctor_app.models.offlineData.database

import androidx.room.*
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.UVEntity
import kotlinx.coroutines.flow.Flow


@Dao
interface DataDao {

    /**** PRE-DIAGNOSTIC CANCER DATA *****/
    @Query("SELECT * FROM cancer_data_table")
    fun readCancerData():kotlinx.coroutines.flow.Flow<List<CancerDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCancerRecord(cancerDataEntity: CancerDataEntity)

    @Delete
    suspend fun deleteCancerRecord(cancerDataEntity: CancerDataEntity)

    @Query("DELETE FROM cancer_data_table")
    suspend fun deleteAllCancerRecords()

    /******* RADIATION WEATHER DATA *********/
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRadiationWeatherData(uvEntity: UVEntity)
    @Query("SELECT * FROM RADIATION_WEATHER_TABLE")
    fun readRadiationWeatherData(): Flow<UVEntity>

}