package com.robertconstantindinescu.my_doctor_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.navigation.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.tabs.TabLayoutMediator
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.viewPagerAdapters.ViewPagerAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDetailsRecipeBinding
import com.robertconstantindinescu.my_doctor_app.models.offlineData.database.entities.FavoritesEntity
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.IngredientsFragment
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.InstructionsFragment
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.RecipeOverViewFragment
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RECIPE_RESULT_KEY
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesMainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_details_recipe.*

@AndroidEntryPoint
class DetailsRecipeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityDetailsRecipeBinding
    private val args by navArgs<DetailsRecipeActivityArgs>()

    private val recipeMainViewModel: RecipesMainViewModel by viewModels()

    private var recipeSaved = false
    private var savedRecipeId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mBinding = ActivityDetailsRecipeBinding.inflate(layoutInflater)
        setContentView(mBinding.root)

        setSupportActionBar(toolbar2)
        toolbar2.setTitleTextColor(ContextCompat.getColor(this, R.color.white))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        val resultBundle = Bundle()
        resultBundle.putParcelable(RECIPE_RESULT_KEY,args.result)

        val viewPager = mBinding.viewPager2
        val adapter = ViewPagerAdapter(resultBundle, supportFragmentManager, lifecycle)

        adapter.addFragment(
            RecipeOverViewFragment(),
            resources.getString(R.string.recipeOverViewTitle)

        )
        adapter.addFragment(
            IngredientsFragment(),
            resources.getString(R.string.ingredientsTitle)

        )
        adapter.addFragment(
            InstructionsFragment(),
            resources.getString(R.string.instructionsTitle)

        )

        viewPager.adapter = adapter
        TabLayoutMediator(tab_layout2, viewPager){ tab, position ->
            tab.text = adapter.getPageTitle(position)

        }.attach()
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.recipe_details_menu, menu)
        val menuItem = menu?.findItem(R.id.save_to_favorites_menu) //find the star menu
        checkSavedRecipes(menuItem!!)
        return true
    }

    private fun checkSavedRecipes(menuItem: MenuItem) {
        recipeMainViewModel.readFavoriteRecipes.observe(this, Observer { favoritesEntity ->
            try {
                for (savedRecipe in favoritesEntity){
                    if (savedRecipe.result.id == args.result.id){
                        changeMenuItemColor(menuItem, R.color.yellow)
                        savedRecipeId = savedRecipe.id
                        recipeSaved = true

                    }else changeMenuItemColor(menuItem, R.color.white)
                }
            }catch (e:Exception){
                Log.d("DetailsActivity", e.message.toString())
            }

        })
    }





    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == android.R.id.home){
            finish()

        }else if(item.itemId == R.id.save_to_favorites_menu && !recipeSaved){
            //when press the star, we will call an other function. and pass the item clicked or selected
            saveToFavorites(item)
        }else if(item.itemId == R.id.save_to_favorites_menu && recipeSaved){
            //si se presiona la estrella y la variabel bool esta a true entonves
            removeFromFavorites(item)
        }

        return super.onOptionsItemSelected(item)


    }

    private fun removeFromFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(
            savedRecipeId,
            args.result
        )
        recipeMainViewModel.deleteFavoriteRecipe(favoritesEntity)
        changeMenuItemColor(item, R.color.white)
        showSnackBar(resources.getString(R.string.removed_from_fav))
        recipeSaved = false
    }

    private fun saveToFavorites(item: MenuItem) {
        val favoritesEntity = FavoritesEntity(
            0,
            args.result
        )
        recipeMainViewModel.insertFavoriteRecipe(favoritesEntity)

        changeMenuItemColor(item, R.color.yellow)

        showSnackBar(resources.getString(R.string.recipe_saved))
        recipeSaved = true
    }

    private fun changeMenuItemColor(menuItem: MenuItem, color: Int) {
        menuItem.icon.setTint(ContextCompat.getColor(this, color))

    }

    private fun showSnackBar(message: String) {
        //the firts parameter is the actual view which is detailslayput because we named it. This is the constraint layout from activity_details_layout
        Snackbar.make(
            mBinding.root,
            message,
            Snackbar.LENGTH_LONG

        ).setAction("Okay"){}.show()
    }
}