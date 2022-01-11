package com.robertconstantindinescu.my_doctor_app.viewmodels

import android.app.Application
import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.AndroidViewModel
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.RadiationFragment.Companion.latitude
import com.robertconstantindinescu.my_doctor_app.ui.fragments.patientFragments.recipes.RadiationFragment.Companion.longitude
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.API_KEY
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_API
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_LATITUDE
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.QUERY_LONGITUDE

class QueryViewModel @ViewModelInject constructor(
    application: Application
): AndroidViewModel(application){

    fun applyRadiationQuery() : HashMap<String, String>{

        val query: HashMap<String, String> = HashMap()
        query[QUERY_LATITUDE] = latitude.toString()
        query[QUERY_LONGITUDE] = longitude.toString()
        query[QUERY_API] = API_KEY

        return query

    }

}