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
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.RecipesFragmentDirections
import org.jsoup.Jsoup
import java.lang.Exception

/**
 * this class is used to bind the Result clas svalues to the layout
 */
class RecipesRowBinding {
    //create a companaion object to acces the data from everywhere even from the layout
    companion object {


        @BindingAdapter("onRecipeClickListener")
        @JvmStatic
        fun onRecipeClickListener(recipeRowLayout: ConstraintLayout, result: Result) {
            recipeRowLayout.setOnClickListener {
                try {
                    //here we store int the action the result
                    val action =
                        RecipesFragmentDirections.actionRecipesFragmentToDetailsRecipeActivity(result)
                    //haces la navegacion dede cualquier punto del contraintLayout usando la action que hemos definido
                    recipeRowLayout.findNavController().navigate(action)
                } catch (e: Exception) {
                    Log.d("onRecipeClickListener", e.toString())
                }
            }
        }



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