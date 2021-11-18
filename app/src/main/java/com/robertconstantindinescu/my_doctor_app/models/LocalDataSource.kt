package com.robertconstantindinescu.my_doctor_app.models


import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.DataDao
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.UVEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class LocalDataSource @Inject constructor(
    private val dataDao: DataDao
){

    /**** PRE-DIAGNOSTIC CANCER DATA ***/
    fun readCancerData():Flow<List<CancerDataEntity>>{
        return dataDao.readCancerData()
    }

    suspend fun insertCancerRecord(cancerDataEntity: CancerDataEntity){
        dataDao.insertCancerRecord(cancerDataEntity)
    }

    suspend fun deleteCancerRecord(cancerDataEntity: CancerDataEntity){
        dataDao.deleteCancerRecord(cancerDataEntity)
    }

    suspend fun deleteAllCancerRecords(){
        dataDao.deleteAllCancerRecords()
    }

    /****** RADIATION WEATHER DATA ******/

    suspend fun insertRadiationWeatherData(uvEntity: UVEntity){
        dataDao.insertRadiationWeatherData(uvEntity)
    }

    fun readRadiationWeatherData(): Flow<UVEntity>{
        return dataDao.readRadiationWeatherData()
    }

}