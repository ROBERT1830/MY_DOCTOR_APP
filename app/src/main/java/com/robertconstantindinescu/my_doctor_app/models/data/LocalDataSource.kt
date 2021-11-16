package com.robertconstantindinescu.my_doctor_app.models.data


import com.robertconstantindinescu.my_doctor_app.models.data.database.CancerDataDao
import com.robertconstantindinescu.my_doctor_app.models.data.database.entities.CancerDataEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val cancerDataDao: CancerDataDao
){

    fun readCancerData():Flow<List<CancerDataEntity>>{
        return cancerDataDao.readCancerData()
    }

    suspend fun insertCancerRecord(cancerDataEntity: CancerDataEntity){
        cancerDataDao.insertCancerRecord(cancerDataEntity)
    }

    suspend fun deleteCancerRecord(cancerDataEntity: CancerDataEntity){
        cancerDataDao.deleteCancerRecord(cancerDataEntity)
    }

    suspend fun deleteAllCancerRecords(){
        cancerDataDao.deleteAllCancerRecords()
    }
}