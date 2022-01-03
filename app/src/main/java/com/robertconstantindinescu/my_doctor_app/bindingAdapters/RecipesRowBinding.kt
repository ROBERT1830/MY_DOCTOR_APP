package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.content.Context
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.os.bundleOf
import androidx.databinding.BindingAdapter
import androidx.navigation.findNavController
import coil.load
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.Result
import org.jsoup.Jsoup
import java.lang.Exception

/**
 * this class is used to bind the Result clas svalues to the layout
 */
class RecipesRowBinding {
    //create a companaion object to acces the data from everywhere even from the layout
    companion object {

        /**
         * Funciton that acts as a click listener to use for clicl in the recycler
         * the firt aprameter is the contraint layout we are actiion on and the second one is
         * the Result model class.
         * Then inside set the onClickListener. RecipesFragmentAction is a class that is created qhen
         * we perfomr an action
         *
         * IMPORTANT : FOR THAT TO ORK WE NEED TO MAKE THE LIST<extendedingredient> parcelized as well, not the list
         * bust the clas itself.
         */
//        @BindingAdapter("onRecipeClickListener")
//        @JvmStatic
//        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: Result) {
//            recipeRowLayout.setOnClickListener {
//                try {
//                    //here we store int the action the result
//                    val action =
//                        RecipesFragmentDirections.actionRecipesFragmentToDetailsActivity(result)
//                    //haces la navegacion dede cualquier punto del contraintLayout usando la action que hemos definido
//                    recipeRowLayout.findNavController().navigate(action)
//                } catch (e: Exception) {
//                    Log.d("onRecipeClickListener", e.toString())
//                }
//            }
//        }



        //crete the function wich will convert integer of number of likes into string
        /**
         * this will have 2 parameters. 1-the actual type of view on which we are going to use this function
         * in that case a we want to display the number in  a textview. 2-the actual type data from the Result class
         * in that case an integer.
         * We need to annotate the function with two anotations
         * @BindingAdapter() and inside we have to specify the name of the atrribute wichh we are going to use inside our
         * recipes row_layot. es como que ese nombre contendra el dato gracias a bindingadapter y lo podras
         * usar en el textview para poner el dato convertido.
         * @JvmSttic() Specifies that an additional static method needs to be generated from this
         * element if it's a function. If this element is a property, additional static
         * getter/setter methods should be generated.
         * with this annotation we are telling the compailer to make our function static
         * to acces this funtion elsewhere in our projects. So when we go to the recipes_row_layout
         * and try to acces this function we will can.
         *
         *
         *
         */
        @BindingAdapter("setNumberOfLikes")
        @JvmStatic
        fun setNumberOfLikes(textView: TextView, likes: Int) {
            textView.text = likes.toString()
        }

        @BindingAdapter("setNumberOfMinutes")
        @JvmStatic
        fun setNumberOfMinutes(textView: TextView, minutes: Int) {
            textView.text = minutes.toString()
        }

        /**
         * here we have to create a newbindig adapter but now for an image view and a text because
         * we will change the color of the icon and then the text. So we can pass as a first parameter
         * a generic view because we will iuse this generic view to actually act in both types of view
         * an imaggeview and a textview.
         */
        @BindingAdapter("applyVeganColor")
        @JvmStatic
        fun applyVeganColor(view: View, vegan: Boolean) {
            if (vegan) {
                when (view) {
                    is TextView -> {
                        //set the text color. the constext needed is getting from the view.
                        view.setTextColor(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                    is ImageView -> {
                        view.setColorFilter(
                            ContextCompat.getColor(
                                view.context,
                                R.color.green
                            )
                        )
                    }
                }
            }
        }

        /**
         * Create a function to bind the image. 2 parameters neded
         * 1-is the actuall view where we use this bindingadapter. so will we the imageview.
         * 2-Te img url from Result
         */
        @BindingAdapter("loadImageFromUrl")
        @JvmStatic
        fun loadImageFromUrl(imageView: ImageView, imageUrl: String) {
            //Use coil library to get the image and display it inside the imageview.
            imageView.load(imageUrl) {
                crossfade(600)
                error(R.drawable.ic_error_placeholder) //we use this to display a placeholder whenever the iamgen is not fetched from database or api
                //only those images that were catched correctluy when there were internet conection will display correctly and the other no
            }
        }

        /**
         * 16---
         * parameters is text view becaus we use this funciont on a atextview
         */

        @BindingAdapter("parseHtml")
        @JvmStatic
        fun parseHtml(textView: TextView, description: String?) {
            if (description != null) {
                //parse html tags
                val desc = Jsoup.parse(description).text()
                textView.text = desc
            }
        }
    }
}