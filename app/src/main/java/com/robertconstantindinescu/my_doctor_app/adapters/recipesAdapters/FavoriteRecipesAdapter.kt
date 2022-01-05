package com.robertconstantindinescu.my_doctor_app.adapters.recipesAdapters

import android.view.*
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.FavoriteRecipesRowLayoutBinding
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.FavoritesEntity
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.FavoritesRecipesFragmentDirections
import com.robertconstantindinescu.my_doctor_app.utils.CancerDiffUtil
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesMainViewModel
import kotlinx.android.synthetic.main.favorite_recipes_row_layout.view.*

class FavoriteRecipesAdapter (
    private val requireActivity: FragmentActivity,
    private val recipesMainViewModel: RecipesMainViewModel
): RecyclerView.Adapter<FavoriteRecipesAdapter.MyViewHolder>(), ActionMode.Callback {

    /**
     * 22
     */

    private lateinit var mActionMode: ActionMode //we need that variable as global to can get out from contextual action mode dinamucally.  and now go to oncreateActionMode
    private lateinit var rootView: View

    private var multiSelection = false
    private var selectedRecipes = arrayListOf<FavoritesEntity>()
    private var myViewHolders = arrayListOf<MyViewHolder>()

    private var favoriteRecipes = emptyList<FavoritesEntity>()

    fun setData(newFavoriteRecipes: List<FavoritesEntity>) {
        val favoriteRecipesDiffUtil = CancerDiffUtil(favoriteRecipes, newFavoriteRecipes)
        val diffUtilResult = DiffUtil.calculateDiff(favoriteRecipesDiffUtil)
        favoriteRecipes = newFavoriteRecipes
        diffUtilResult.dispatchUpdatesTo(this)
    }

    //if dont se this class FavoriteRecipesRowLayoutBinding creted when we create  laoyut for bindig rebuild the project
    class MyViewHolder(private val binding: FavoriteRecipesRowLayoutBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(favoritesEntity: FavoritesEntity) { //here we bind the varibale from recipesrow layout to the favoritesEntity of the function
            binding.favoritesEntity = favoritesEntity
            binding.executePendingBindings() //this update all the views
        }

        companion object {
            fun from(parent: ViewGroup): MyViewHolder {
                val layoutInflater = LayoutInflater.from(parent.context)
                val binding = FavoriteRecipesRowLayoutBinding.inflate(layoutInflater, parent, false)
                return MyViewHolder(binding)
            }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {

        return MyViewHolder.from(parent)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        myViewHolders.add(holder) //añades el holder a la laista de holders
        rootView =
            holder.itemView.rootView //store the rootview inside the global variable we need that ato acces it from showSnackbar

        val currentRecipe = favoriteRecipes[position]
        holder.bind(currentRecipe)

        /**
         * Single Clicl Listener
         * So we are going to apply the selecion or that style for out recipe only if a multiselection is true
         *
         * ehere we only apply this style if multiseelction is true. that menas that we are not going accidently to our
         * details activity when we are making a selections in our list
         */
        holder.itemView.favoriteRecipesRowLayout.setOnClickListener {
            /**22*/
            if (multiSelection) {
                applySelection(holder, currentRecipe)
            } else {
                //CREATE the action for the onlcicklistener using the class that is generated when we created the action betwen
                //favfragment and detailActivity. We have to pass an argument which is the result. taht contains the actual class Result with theh data from a recipe.
                val action =
                    FavoritesRecipesFragmentDirections.actionBtnFavRecipesToDetailsRecipeActivity(
                        currentRecipe.result
                    )
                //PERFOR THE ACTION to navigate using the action we defined or created before
                holder.itemView.findNavController().navigate(action)
            }


        }

        /**
         * Long Clicl Listener
         *  The default value of the multiselection is false. So when we press onlonkg click
         *  we change the value to ture. Inside the if block we chaque the value of multiSelection
         *  and by defauklt is false. And when we lonkg press change the value tu ture
         *  then we are going to open the action mode using the requireaCTIITY and aply selection
         *  else multiSelection is false.
         */
        holder.itemView.favoriteRecipesRowLayout.setOnLongClickListener {
            if (!multiSelection) {
                multiSelection = true
                //firts when we press long we need to start the context action mode
                //but we need one more paarameter inside the favorite recipe adapter
                /*that is the fragment activity as a parameter to the constructor above
                * to start the contextual action model*/
                requireActivity.startActionMode(this) //this refer to the recipes fav adapater because implement the interface callback
                applySelection(holder, currentRecipe)
                true
            } else {
                multiSelection = false
                false
            }

        }


    }

    override fun getItemCount(): Int {
        return favoriteRecipes.size
    }

    /**
     * 22-- function to apply the  selection mode
     * this funciton will be called from the click listeners.
     */
    private fun applySelection(holder: MyViewHolder, currentRecipe: FavoritesEntity) {
        /*Here we call the selectedRecipe variable and we will add or remove items from this arraylist
        * */
        if (selectedRecipes.contains(currentRecipe)) {
            selectedRecipes.remove(currentRecipe) //esta variable solo actua para poder comprobar si lo que hemos clicado esta en el array y asi cambiar el color del background o no o devolverlo al original en caso de que ya este y lo eliminamos.
            changeRecipeStyle(
                holder,
                R.color.cardBackgroundColor,
                R.color.strokeColor
            ) //a white color and medium gray the same color that we have normally
            applyActionModeTitle()
        } else {
            selectedRecipes.add(currentRecipe)
            changeRecipeStyle(holder, R.color.cardBackgroundLightColor, R.color.colorPrimary)
            applyActionModeTitle()
        }
    }

    private fun applyActionModeTitle() {
        /**when there are no recipes insithe the arraylist of selected recipes. so size is 0
         * call mActionMode.finish() method
         * this funciton wiil be called from applySelection function*/
        when (selectedRecipes.size) {
            0 -> {
                mActionMode.finish()
            }
            1 -> { //if we select only one recipe change the title
                mActionMode.title = "${selectedRecipes.size} item selected"


            }
            //when we have more than one recipe selected
            else -> {
                mActionMode.title = "${selectedRecipes.size} items selected"
            }

        }
    }

    /**
     * 22
     * this function will be called from the applySelection two times in if and elese block
     */
    private fun changeRecipeStyle(holder: MyViewHolder, backgroundColor: Int, strokeColor: Int) {
        /*here we change the color of favori_recipe_Row_laupout background and the stroke of the material
        * cardview
        * favoriteRecipesRowLayout is the id from contraintlayout in favori_recipe_Row_laupout
        * */
        holder.itemView.favoriteRecipesRowLayout.setBackgroundColor(
            ContextCompat.getColor(requireActivity, backgroundColor)
        )
        holder.itemView.favorite_row_cardView.strokeColor =
            ContextCompat.getColor(requireActivity, strokeColor)
    }


    /**
     * 22
     * here we will inflate the menu we have created with the rubishusing the menu  inflater
     * not the layout inflater
     */
    override fun onCreateActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        actionMode?.menuInflater?.inflate(R.menu.favorites_contextual_menu, menu)
        mActionMode =
            actionMode!! //set the global variable mActionMode to the actionMode of here. so from this function we get the actionmode and store taht in a global variable. Becasue we wil need taht to set the title of contextual action mode
        applyStatusBarColor(R.color.contextualStatusBarColor)

        return true
    }

    override fun onPrepareActionMode(actionMode: ActionMode?, menu: Menu?): Boolean {
        return true
    }

    override fun onActionItemClicked(actionMode: ActionMode?, menu: MenuItem?): Boolean {
        /**check if the clicked item from the context action bar is the rubish */
        if (menu?.itemId == R.id.delete_favorite_recipe_menu) {
            //then get the selected recipe array list we created and call foreach. and iside it
            //cal mainViremodel  and delete and pass the entity o entities to delete.
            /*So here we get through each and every item isnide the arrya and delete all the recipes wich a
            * are selected. one by one */
            selectedRecipes.forEach {
                recipesMainViewModel.deleteFavoriteRecipe(it)
            }
            showSnackBar("${selectedRecipes.size} Recipe/s removed.")
            multiSelection = false //because we deleted the selected one
            selectedRecipes.clear() //clear the arraylist because we reome them from the dataase
            actionMode?.finish()
        }
        return true
    }

    override fun onDestroyActionMode(actionMode: ActionMode?) {
        /**For each holder i am going to call this cahcngeRecipeStyle to change again to the normal style
         * So when we press the back button we retunr the style to the normal */
        myViewHolders.forEach { holder ->
            changeRecipeStyle(holder, R.color.cardBackgroundColor, R.color.strokeColor)
        }
        /**22*/
        multiSelection = false
        selectedRecipes.clear() //remove all items from selectedrecipe array.  when close the action mode
        applyStatusBarColor(R.color.statusBarColor)

    }

    /**
     * 22
     * thi funciton will change the status bar color so we need to pass a color int
     * this funciton will be called on onCreateActionMode and onDestroy when we get out of the contextual action mode
     */
    private fun applyStatusBarColor(color: Int) {
        requireActivity.window.statusBarColor =
            ContextCompat.getColor(requireActivity, color)

    }

    private fun showSnackBar(message: String) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).setAction("OK") {}.show()
    }

    /**
     * 22
     * ehre we check if the contetual action mode is initialized or if it is visible, so then close it
     */
    fun clearContextualAcitonMode() {
        if (this::mActionMode.isInitialized) {
            mActionMode.finish()
        }

    }
}