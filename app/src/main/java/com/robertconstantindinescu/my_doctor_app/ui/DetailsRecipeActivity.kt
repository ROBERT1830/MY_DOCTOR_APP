package com.robertconstantindinescu.my_doctor_app.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.core.content.ContextCompat
import androidx.navigation.navArgs
import com.google.android.material.tabs.TabLayoutMediator
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.viewPagerAdapters.ViewPagerAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.ActivityDetailsRecipeBinding
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.IngredientsFragment
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.InstructionsFragment
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.RecipeOverViewFragment
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RECIPE_RESULT_KEY
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_details_recipe.*

class DetailsRecipeActivity : AppCompatActivity() {
    private lateinit var mBinding: ActivityDetailsRecipeBinding
    private val args by navArgs<DetailsRecipeActivityArgs>()


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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()


        return super.onOptionsItemSelected(item)


    }

}