package com.robertconstantindinescu.my_doctor_app.adapters.recipesAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import coil.load
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.ExtendedIngredient
import com.robertconstantindinescu.my_doctor_app.utils.CancerDiffUtil
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.BASE_IMAGE_URL
import kotlinx.android.synthetic.main.ingredients_row_layout.view.*

class IngredientsAdapter: RecyclerView.Adapter<IngredientsAdapter.MyViewHolder>() {

    private var ingredientsList = emptyList<ExtendedIngredient>()

    class MyViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.ingredients_row_layout, parent, false))


    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        //add the url and the image from the data ExtendedIngredient class
        holder.itemView.ingredient_imageView.load(BASE_IMAGE_URL + ingredientsList[position].image){
            crossfade(600)
            error(R.drawable.ic_error_placeholder)
        }
        holder.itemView.ingredient_name.text = ingredientsList[position].name
        holder.itemView.ingredient_amount.text = ingredientsList[position].amount.toString()
        holder.itemView.ingredient_unit.text = ingredientsList[position].unit
        holder.itemView.ingredient_consistency.text = ingredientsList[position].consistency
        holder.itemView.ingredient_original.text = ingredientsList[position].original

    }

    override fun getItemCount(): Int {
        return ingredientsList.size

    }

    fun setData(newIngredients: List<ExtendedIngredient>){
        val ingredientsDiffUtil =
            CancerDiffUtil(ingredientsList, newIngredients)
        val diffUtilResult = DiffUtil.calculateDiff(ingredientsDiffUtil)
        ingredientsList = newIngredients
        diffUtilResult.dispatchUpdatesTo(this)
    }

}