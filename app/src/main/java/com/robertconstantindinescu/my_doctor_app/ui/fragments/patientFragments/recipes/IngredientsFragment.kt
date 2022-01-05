package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.recipesAdapters.IngredientsAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentIngredientsBinding
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.Result
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_ingredients.view.*


class IngredientsFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var myBundle: Result? = null
    private lateinit var mBinding: FragmentIngredientsBinding
    private val mAdapter: IngredientsAdapter by lazy { IngredientsAdapter() }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            myBundle = it.getParcelable(RECIPE_RESULT_KEY)

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentIngredientsBinding.inflate(layoutInflater)
        setUpRecyclerView(mBinding.root)

        myBundle?.extendedIngredients?.let{mAdapter.setData(it)}

        return mBinding.root
    }

    private fun setUpRecyclerView(view: View){
        view.ingredients_recyclerview.adapter = mAdapter
        view.ingredients_recyclerview.layoutManager = LinearLayoutManager(requireContext())

    }


}