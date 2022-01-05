package com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import coil.load
import com.robertconstantindinescu.my_doctor_app.R
import com.robertconstantindinescu.my_doctor_app.databinding.FragmentRecipeOverViewBinding
import com.robertconstantindinescu.my_doctor_app.models.onlineData.recipesModels.Result
import com.robertconstantindinescu.my_doctor_app.utils.Constants
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.RECIPE_RESULT_KEY
import kotlinx.android.synthetic.main.fragment_recipe_over_view.*
import org.jsoup.Jsoup


class RecipeOverViewFragment : Fragment() {
   private lateinit var mBinding: FragmentRecipeOverViewBinding
   //private lateinit var myBundle: Result

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            //myBundle = it.getParcelable<Result>(RECIPE_RESULT_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentRecipeOverViewBinding.inflate(layoutInflater)

        val args = arguments
        val myBundle:Result? = args?.getParcelable(RECIPE_RESULT_KEY)

        with(mBinding){
            mainImageView.load(myBundle?.image)
            titleTextView.text = myBundle?.title
            likesTextView.text = myBundle?.aggregateLikes.toString()
            timeTextView.text = myBundle?.readyInMinutes.toString()

            myBundle?.summary.let {
                var summary = Jsoup.parse(it).text()
                summaryTextView.text = summary

            }

            if(myBundle?.vegetarian == true){
                vegetarianImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                vegetarianTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

            }

            if(myBundle?.vegan == true){
                veganImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                veganTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

            }

            if(myBundle?.glutenFree == true){
                glutenFreeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                glutenFreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

            }

            if(myBundle?.dairyFree == true){
                dairyFreeImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                dairyFreeTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

            }

            if(myBundle?.veryHealthy == true){
                healthyImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                healthyTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))

            }
            if (myBundle?.nutrition?.nutrients!!.isNotEmpty()){
                if(myBundle?.nutrition?.nutrients[0].amount != 0.0){
                    vitaminAImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminATextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminAAmountTextView.text = "${myBundle.nutrition.nutrients[0].amount.toString()} IU"
                }

                if(myBundle?.nutrition?.nutrients[1].amount != 0.0){
                    vitaminEImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminETextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminEAmountTextView.text = "${myBundle.nutrition.nutrients[1].amount.toString()} mg"
                }

                if(myBundle?.nutrition?.nutrients[2].amount != 0.0){
                    vitaminCImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminCTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminCAmountTextView.text = "${myBundle.nutrition.nutrients[2].amount.toString()} mg"
                }

                if(myBundle?.nutrition?.nutrients[3].amount != 0.0){
                    vitaminDImageView.setColorFilter(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminDTextView.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
                    vitaminDAmountTextView.text = "${myBundle.nutrition.nutrients[3].amount.toString()} μg"
                }

            }

        }


        return mBinding.root
    }


}