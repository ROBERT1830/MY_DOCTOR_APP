package com.robertconstantindinescu.my_doctor_app.models.data.database

import androidx.room.*
import com.robertconstantindinescu.my_doctor_app.models.data.database.entities.CancerDataEntity


@Dao
interface CancerDataDao {
    @Query("SELECT * FROM cancer_data_table")
    fun readCancerData():kotlinx.coroutines.flow.Flow<List<CancerDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCancerRecord(cancerDataEntity: CancerDataEntity)

    @Delete
    suspend fun deleteCancerRecord(cancerDataEntity: CancerDataEntity)

    @Query("DELETE FROM cancer_data_table")
    suspend fun deleteAllCancerRecords()

}