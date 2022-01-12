package com.robertconstantindinescu.my_doctor_app.utils

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import androidx.annotation.RequiresApi



class CheckInternet {

    companion object{
        @RequiresApi(Build.VERSION_CODES.M)
         fun hasInternetConnection(context: Context): Boolean {
            /*to check if the app has internet conection firts we get the application class
            * and the getSystemService return A ConnectivityManager for handling management of network connections.*/
            //
            val connectivityManager = context.getSystemService(
                Context.CONNECTIVITY_SERVICE
            ) as ConnectivityManager

            val activeNetwork = connectivityManager.activeNetwork
                ?: return false // activeNetwork--->Returns a Network (si tienes coenxion) object corresponding to the currently active default data network This will return null when there is no default network.
            val capabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
                ?: return false  // lo que hace es coger e identificar la capacidad de red, es decir si hay o no capacidad de tner red con el objeto network que identifica la red y que pasamos cmo parmetro. Si con esa red de internet no tenemos capacidad de conexion devuelveme falso.
            //se coge ee objeto networ y se usa pra ver que tipo de conexxion estamos teniendo
            return when {
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false


            }
        }
    }

}