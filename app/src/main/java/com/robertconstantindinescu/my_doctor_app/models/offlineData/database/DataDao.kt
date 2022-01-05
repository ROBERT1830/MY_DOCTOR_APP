package com.robertconstantindinescu.my_doctor_app.models.offlineData.database

import androidx.room.*
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.CancerDataEntity
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.FavoritesEntity
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.RecipesEntity
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

    /****** RECIPE DATA **********/

    @Query("SELECT * FROM recipes_table ORDER BY id ASC")
    fun readRecipes(): Flow<List<RecipesEntity>>

    @Query("SELECT * FROM favorite_recipes_table ORDER BY id ASC")
    fun readFavoriteRecipes(): Flow<List<FavoritesEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRecipes(recipesEntity: RecipesEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Delete
    suspend fun deleteFavoriteRecipe(favoritesEntity: FavoritesEntity)

    @Query("DELETE FROM favorite_recipes_table")
    suspend fun deleteAllFavoriteRecipes()
}