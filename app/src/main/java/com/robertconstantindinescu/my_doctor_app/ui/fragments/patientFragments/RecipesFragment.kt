package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.robertconstantindinescu.my_doctor_app.R


class RecipesFragment : Fragment() {


    /**
     * API REQUEST TO THE RECIPES.
     * https://api.spoonacular.com/recipes/complexSearch?cusine=italian&addRecipeInformation=true&number=10&fillIngredients=true&diet=vegetarian&maxVitaminA=50&maxVitaminC=50&maxVitaminD=50&maxVitaminE=50&apiKey=fc9a88b126ac432786da2b6181c073d5
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recipes, container, false)
    }


}