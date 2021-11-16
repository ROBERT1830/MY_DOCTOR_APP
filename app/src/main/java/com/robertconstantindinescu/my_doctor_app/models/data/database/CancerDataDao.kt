package com.robertconstantindinescu.my_doctor_app.models.data.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.robertconstantindinescu.my_doctor_app.models.data.database.entities.CancerDataEntity


@Dao
interface CancerDataDao {
    @Query("SELECT * FROM cancer_data_table")
    fun readCancerData():kotlinx.coroutines.flow.Flow<List<CancerDataEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCancerDiagnostic(cancerDataEntity: CancerDataEntity)
}