package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.RecipesEntity
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.FoodRecipeResponse
import com.robertconstantindinescu.my_doctor_app.utils.NetworkResult

/**for the layout fragment_recipes*/
class RecipesBinding {
    companion object{
        /**
         * two functions will be addd here
         * 1- for the iamge
         * 2-for the text
         */


        /**parameters
         * 1-the view on wher ewe are going to use this custom binding adapter. In a imageview
         * 2- api response with theNetworkresponse
         * 3-Database List<RecipesEntitiy>*/
        //here we specify two atribute because those are gona be used in the fragment_recipe layout. requireall menas that we want to make our compiler display a warning error if we specify only one atribute.
        @BindingAdapter("readApiResponse", "readDatabase", requireAll = true)
        //esos dos parametros son los que se cogen en el layout pq lo hemos bindeado con el viewmodel
        // y desde ahi como podemos acceder al viewmodel pues le mandas el resultado ambas variables
        // livedata que son readRecipes para la base de datos y recipesResponse para la api. y aqui se hace una evaluacion de ellas
        //y como automaticametne se coge la imageview y se pasa como parametro pues podemos modificar
        //cosas de ella como su visibilidad en funcion del estado de lso dos parametros que pasamos.
        //como las variables qeu se pasasn son lifedata, necesitamos haber especificado el ciclo de vida
        //del fragmento donde esta la vista que vamos a manipular dinamicamente.

        @JvmStatic
        fun errorImageViewVisibility(
            imageView: ImageView,
            apiResponse: NetworkResult<FoodRecipeResponse>?,
            database: List<RecipesEntity>?
        ){
            //cuando hay error en el api y en la base de dato este facia mostramos l aiamgen triste. Con esto modificamos la visibiliadd de la imagen
            if(apiResponse is NetworkResult.Error && database.isNullOrEmpty()){
                imageView.visibility = View.VISIBLE
            }else if(apiResponse is NetworkResult.Loading){
                imageView.visibility = View.INVISIBLE
            }else if (apiResponse is NetworkResult.Success){
                imageView.visibility = View.INVISIBLE
            }
        }

        @BindingAdapter("readApiResponse2", "readDatabase2", requireAll = true)
        @JvmStatic
        fun errorTextViewVisibility(
            textView: TextView,
            apiResponse: NetworkResult<FoodRecipeResponse>?,
            database: List<RecipesEntity>?  //puede que no tengas datos de al base de datos? si
        ){
            if(apiResponse is NetworkResult.Error && database.isNullOrEmpty()){
                textView.visibility = View.VISIBLE
                textView.text = apiResponse.message.toString() //acedes al mensaje de error que se alamcena en network.
            }else if(apiResponse is NetworkResult.Loading){
                textView.visibility = View.INVISIBLE
            }else if (apiResponse is NetworkResult.Success){
                textView.visibility = View.INVISIBLE
            }
        }
    }
}