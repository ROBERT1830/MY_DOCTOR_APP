package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.recipesAdapters.RecipesAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentRecipesBinding
import com.robertconstantindinescu.my_doctor_app.utils.NetworkListener
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesMainViewModel
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesQueryUtilsViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class RecipesFragment : Fragment() {


    /**
     * API REQUEST TO THE RECIPES.
     * https://api.spoonacular.com/recipes/complexSearch?cusine=italian&addRecipeInformation=true&number=10&fillIngredients=true&diet=vegetarian&maxVitaminA=50&maxVitaminC=50&maxVitaminD=50&maxVitaminE=50&apiKey=fc9a88b126ac432786da2b6181c073d5
     */

    private var _binding: FragmentRecipesBinding? = null
    private val binding get() = _binding!!

    private lateinit var recipesMainViewModel: RecipesMainViewModel
    private lateinit var recipesQueryUtilsViewModel: RecipesQueryUtilsViewModel
    private val mAdapter by lazy { RecipesAdapter() }

    private lateinit var networkListener: NetworkListener


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }

        recipesMainViewModel =
            ViewModelProvider(requireActivity()).get(RecipesMainViewModel::class.java)
        recipesQueryUtilsViewModel =
            ViewModelProvider(requireActivity()).get(RecipesQueryUtilsViewModel::class.java)


    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentRecipesBinding.inflate(inflater, container, false)

        binding.lifecycleOwner = this
        binding.recipesMainViewModel = recipesMainViewModel

        setHasOptionsMenu(true)
        setUpRecyclerView()


        recipesQueryUtilsViewModel.readBackOnline.observe(viewLifecycleOwner, Observer {
            recipesQueryUtilsViewModel.backOnline = it
        })

        lifecycleScope.launch {
            networkListener = NetworkListener()
            networkListener.checkNetworkAvailability(requireContext()).collect { status ->

                Log.d("NetworkListener", status.toString())
                recipesQueryUtilsViewModel.networkStatus = status
                recipesQueryUtilsViewModel.showNetworkStatus()
                //readDatabase()

            }
        }

        return binding.root
    }

    private fun setUpRecyclerView() {
        binding.recyclerview.adapter = mAdapter //el adapter se inicia
        binding.recyclerview.layoutManager = LinearLayoutManager(requireContext())
        showShimmerEffec()
    }

    private fun showShimmerEffec(){

        binding.recyclerview.showShimmer()
    }

    private fun hideShimmerEffect(){
        binding.recyclerview.hideShimmer()
    }

}