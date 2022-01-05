package com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.Result
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.FAVORITE_RECIPES_TABLE

@Entity(tableName = FAVORITE_RECIPES_TABLE)
class FavoritesEntity (
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    var result: Result
)