package com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.FoodRecipeResponse
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RECIPES_TABLE

/**
 * The table will contain only one row which will contain that object of FoodRecipe which in order contains list of result
 */
@Entity(tableName = RECIPES_TABLE)
class RecipesEntity(var foodRecipe: FoodRecipeResponse) { //WE CANT store comple object inside the dabase so we need to use typeconverter and parse the data on those objects in a serialized JSONO or a string. And when we read from database we convert from strign or serialized JSON to the origina FoodRecipe object which have a list of Result.
    @PrimaryKey(autoGenerate = false) //whenever we fetch a new data from out api we are going to replace all the data
    // from the database with the new data. Por eso lo ponemosa falso par aque si metemos nuevos datos de al api no se genere otra fial.
    //so when our applicatoin reads our database, it will fetch the newest data posible because we only have one row.
    var id: Int = 0
}