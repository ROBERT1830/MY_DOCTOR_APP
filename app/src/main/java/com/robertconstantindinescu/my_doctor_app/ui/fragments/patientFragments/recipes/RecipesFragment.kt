package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.adapters.recipesAdapters.RecipesAdapter
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentRecipesBinding
import com.robertconstantindinescu.my_doctor_app.utils.NetworkListener
import com.robertconstantindinescu.my_doctor_app.utils.NetworkResult
import com.robertconstantindinescu.my_doctor_app.utils.observeOnce
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

    private val args by navArgs<RecipesFragmentArgs>()
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
                readDatabase()

            }
        }

        binding.recipesFab.setOnClickListener {
            /**Only if recipesViewModel.networkStatus is true  then we want to navigate to the bottom sheet
             * if there is no internet conection we cant.
             * si intentamos hacer lcick en el fab y no tenemos internet nonos va a dejar y nos dria que no hay conexion.
             * */
            if(recipesQueryUtilsViewModel.networkStatus){
                findNavController().navigate(R.id.action_recipesFragment_to_recipesBottomSheet) // con el controlador navegamos y le ponemos la accion que hemos indicado con la flecha en el my_nav.
            }else{
                recipesQueryUtilsViewModel.showNetworkStatus()
            }

        }

        return binding.root
    }



    private fun readDatabase() {

        lifecycleScope.launch {

            recipesMainViewModel.readRecipes.observeOnce(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty() && !args.backFromBottomSheet){ //means that the database is not empty and have some data so we want to display in the reculcerview.
                    Log.d("RecipesFragment", "readDatabase called!")
                    mAdapter.setData(database[0].foodRecipe)
                    hideShimmerEffect()
                }else {

                    requestApiData()
                }
            })
        }

    }

    private fun requestApiData(){
        Log.d("RecipesFragment", "requestApiData called!")
        recipesMainViewModel.getRecipes(recipesQueryUtilsViewModel.applyQueries()) //--> use the RecipeViewModel eich contains the endpoint queries. We use thar for not havving the applyquery function here and make it clenear.
        recipesMainViewModel.recipesResponse.observe(viewLifecycleOwner, Observer { response ->
            when(response){
                is NetworkResult.Success ->{
                    //hide shimmer response becase we getdata
                    hideShimmerEffect()
                    //get acces to the food recipe
                    //accedes a alos datos de NetworkResult que es una clase foodRecipe qeu tiene una lista de result y le pasas esa lsita al adapter.
                    response.data?.let {
                        mAdapter.setData(it) }
                }
                is NetworkResult.Error ->{
                    hideShimmerEffect()
                    loadDataFromCache()
                    Toast.makeText(
                        requireContext(), response.message.toString(),
                        Toast.LENGTH_SHORT).show()
                }
                is NetworkResult.Loading -> {
                    showShimmerEffec()
                }
            }
        })
    }

    private fun loadDataFromCache(){
        lifecycleScope.launch {
            recipesMainViewModel.readRecipes.observe(viewLifecycleOwner, Observer { database ->
                if (database.isNotEmpty()){
                    mAdapter.setData(database[0].foodRecipe)
                }
            })
        }

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
    override fun onDestroy() {
        super.onDestroy()
        /**with this we avoid the ememory leaks
         * so when the recipe fragment is destroyed this binding will be set to null. */
        _binding = null
    }

}
