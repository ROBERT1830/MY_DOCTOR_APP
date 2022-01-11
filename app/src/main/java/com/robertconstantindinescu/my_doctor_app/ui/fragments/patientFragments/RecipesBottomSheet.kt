package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import androidx.navigation.fragment.findNavController
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_CUISINE_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_DIET_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_MEAL_TYPE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_VITAMIN_A
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_VITAMIN_C
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_VITAMIN_D
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.DEFAULT_VITAMIN_E
import com.robertconstantindinescu.my_doctor_app.viewmodels.RecipesQueryUtilsViewModel
import kotlinx.android.synthetic.main.fragment_recipes_bottom_sheet.view.*
import java.lang.Exception
import java.util.*
import androidx.appcompat.app.AppCompatActivity





class RecipesBottomSheet : Fragment() /*RecipesBottomSheetInterface*/ {

    private  lateinit var recipesQueryUtilsViewModel: RecipesQueryUtilsViewModel

    /**Create the different variables for the chips that are needed to pass as a parameter for the saveMealAndDietType
     * function in RecipesViewModel. */
    private var mealTypeChip = DEFAULT_MEAL_TYPE
    private var mealTypeChipId = 0 //this id represents the actuall id for one of those chip of our chip group. the first one
    private var dietTypeChip = DEFAULT_DIET_TYPE
    private var dietTypeChipId = 0
    private var cuisineTypeChip = DEFAULT_CUISINE_TYPE
    private var cuisineTypeChipId = 0
    private var vitaminA = "0"
    private var vitaminAId = 0
    private var vitaminE = "0"
    private var vitaminEId = 0
    private var vitaminC = "0"
    private var vitaminCId = 0
    private var vitaminD = "0"
    private var vitaminDId = 0

    companion object{
        var vitaminAChecked = false
        var vitaminEChecked = false
        var vitaminCChecked = false
        var vitaminDChecked = false
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
        //activity?.actionBar?.hide()
        //(activity as AppCompatActivity?)!!.supportActionBar!!.hide()
        recipesQueryUtilsViewModel = ViewModelProvider(requireActivity()).get(RecipesQueryUtilsViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val mView = inflater.inflate(R.layout.fragment_recipes_bottom_sheet, container, false)

        recipesQueryUtilsViewModel.readMealAndDietType.asLiveData().observe(viewLifecycleOwner, androidx.lifecycle.Observer { value ->
            //de este readMealAndDietType que es un  vamosFlow<MealAndDietType convertido a livedata vamos  a coger dos valores
            //y almacenarlos en mealTypeChip y dietTypeChip par amostrar los chip selecioando
            mealTypeChip = value.selectedMealType
            dietTypeChip = value.selectedDietType
            cuisineTypeChip = value.selectedCuisineType
            /**Siempre que abramos el dialogbottom el observer se llamara pq se hace una lectura de la datastore preference
             * y con esos datos que recogemos se los asignamos a esas variables
             * Ahora usaremos esas variables para hacer la seleccion de esos chip y mostrale al usaurio su anterior selecion para ello usaremos
             * una funcion en al que pasarmeo los id del chip y el grupo al que pertence*/

            updateChip(value.selectedMealTypeId, mView.mealType_chipGroup)
            updateChip(value.selectedDietTypeId, mView.dietType_chipGroup)
            updateChip(value.selectedCuisineTypeId, mView.cuisineType_chipGroup)
        })

        //generateCuisineChips(mView)
        //accedemos al mealtype chip group
        mView.mealType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            //de ese grupo de chip vamos a seleccionar el id del que se ha chequeado usando findview el cual tiene como tipo de
            //busqueda una vista de tipo Chip. Es decir que poneindole la clase Chip el sabe que tipo de vista tiene que buscar
            val chip = group.findViewById<Chip>(selectedChipId) //entonces en chip tendremos ese id
            val selectedMealType = chip.text.toString().lowercase(Locale.ROOT) //almavenas el texto del chip con el id que hemos detemrinado arriba
            //set the values of the top variables
            mealTypeChip = selectedMealType
            mealTypeChipId = selectedChipId
        }

        mView.dietType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId) //entonces en chip tendremos ese id
            val selectedDietType = chip.text.toString().lowercase(Locale.ROOT) //almavenas el texto del chip con el id que hemos detemrinado arriba
            //set the values of the top variables
            dietTypeChip = selectedDietType
            dietTypeChipId = selectedChipId
        }
        
        mView.cuisineType_chipGroup.setOnCheckedChangeListener { group, selectedChipId ->
            val chip = group.findViewById<Chip>(selectedChipId)
            val selectedCuisineType = chip.text.toString().lowercase(Locale.ROOT)
            
            cuisineTypeChip = selectedCuisineType
            cuisineTypeChipId = selectedChipId
            
        }

        mView.vitaminA.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                vitaminA =   DEFAULT_VITAMIN_A
                vitaminAId = R.id.vitaminA
                vitaminAChecked = true

            }else{

                vitaminAChecked = false
            }
        }
        mView.vitaminE.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                vitaminE =   DEFAULT_VITAMIN_E
                vitaminEId = R.id.vitaminE
                vitaminEChecked = true
            }else{

                vitaminEChecked = false
            }
        }
        mView.vitaminC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                vitaminC =   DEFAULT_VITAMIN_C
                vitaminCId = R.id.vitaminC
                vitaminCChecked = true
            }else{

                vitaminCChecked = false
            }
        }
        mView.vitaminD.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked){
                vitaminD =   DEFAULT_VITAMIN_D
                vitaminDId = R.id.vitaminD
                vitaminDChecked = true
            }else{
                vitaminDChecked = false
            }
        }

        //lsitener to apply button
        mView.apply_btn.setOnClickListener {

            /**here we are going to use our datastore through the recipes viewmodel*/
            recipesQueryUtilsViewModel.saveMealAndDietType(
                mealTypeChip,
                mealTypeChipId,
                dietTypeChip,
                dietTypeChipId,
                cuisineTypeChip,
                cuisineTypeChipId,
                vitaminA,
                vitaminAId,
                vitaminE,
                vitaminEId,
                vitaminC,
                vitaminCId,
                vitaminD,
                vitaminDId

            )
            /**when we prpess the button apply set the boolean argument to true*/
            val action = RecipesBottomSheetDirections.actionRecipesBottomSheetToRecipesFragment(true)
            /**And when we navigate to the recipe fragment we will pas the argument seted to true*/
            findNavController().navigate(action)


        }

        return mView
    }





    private fun updateChip(chipId: Int, chipGroup: ChipGroup) {
        //our chip id will not be 0 if we hav emade a differnte selecion from the first one nd saved the data in the datastore
        if (chipId != 0){
            try {
                chipGroup.findViewById<Chip>(chipId).isChecked = true

            }catch (e: Exception){
                Log.d("RecipesBottomSheet", e.message.toString())
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()

    }




}