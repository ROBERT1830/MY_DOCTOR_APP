package com.robertconstantindinescu.my_doctor_app.adapters.recipesAdapters

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.robertconstantindinescu.my_doctor_app.databinding.RecipesRowLayoutBinding
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.FoodRecipeResponse
import com.robertconstantindinescu.my_doctor_app.utils.CancerDiffUtil
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.Result

class RecipesAdapter: RecyclerView.Adapter<RecipesAdapter.MyViewHolder>() {


    private var recipes = emptyList<Result>()


    fun setData(newData: FoodRecipeResponse){
        val recipesDiffUtil = CancerDiffUtil(recipes, newData.results)
        val diffUtilResult  = DiffUtil.calculateDiff(recipesDiffUtil)

        recipes = newData.results

        diffUtilResult.dispatchUpdatesTo(this)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        //we get the parent from this function
        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val currentRecipe = recipes[position]
        holder.bind(currentRecipe)

    }

    override fun getItemCount(): Int {
        return  recipes.size
    }


     class MyViewHolder(private val binding: RecipesRowLayoutBinding): //es una clase que genera el sistema con todos los compoenetnes entnoeces puedes usar binding para acceder a todos los elementos del layout
        RecyclerView.ViewHolder(binding.root) { //we passe the root of the rowlayout

        fun bind(result: Result){

            binding.result = result
            binding.executePendingBindings()

        }

        companion object{
            fun from(parent: ViewGroup): MyViewHolder{
                val layoutInflater = LayoutInflater.from(parent.context) //preparas el recylcer para aceptar el inlflado
                val binding = RecipesRowLayoutBinding.inflate(layoutInflater, parent, false) //inflas el layout
                return MyViewHolder(binding)
            }
        }

    }


}