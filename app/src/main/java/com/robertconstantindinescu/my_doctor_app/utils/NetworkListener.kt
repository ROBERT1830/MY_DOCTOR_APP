package com.robertconstantindinescu.my_doctor_app.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import kotlinx.coroutines.flow.MutableStateFlow

class NetworkListener : ConnectivityManager.NetworkCallback() {

    private val isNetworkAvailable = MutableStateFlow(false)

    fun checkNetworkAvailability(context: Context): MutableStateFlow<Boolean> {

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        connectivityManager.registerDefaultNetworkCallback(this)
        var isConnected = false
        //tell the amanger to Return an array of all Network currently tracked by the framework.
        //manager dime todas lsa formas de conexxion a als qeu ya esta conectado mi dispositivo
        connectivityManager.allNetworks.forEach {  network ->
            //check the connection cabability to internet of each  network that we have and check if we are online or not.
            val networkCapability = connectivityManager.getNetworkCapabilities(network) //si hay capacidad de conectarse a esa forma de red
            networkCapability?.let {
                //if there is a capability to internet connection
                if (it.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)){ //de esa forma de conexxion, indicame si el dispositivo obtiene coenxxion. Hazme la conexcion
                    //Baciscally here we are checking if our device has intenet connectionand if it hs we want
                    //to set the value of our isConected variable to true. And retun it.
                    isConnected = true
                    return@forEach //devolvemos el resultado del foraech si hay conexion de una forma nos salimos del foreach
                }
            }
        }
        isNetworkAvailable.value = isConnected
        return isNetworkAvailable

    }

    override fun onAvailable(network: Network) {
        isNetworkAvailable.value = true
    }

    override fun onLost(network: Network) {
        isNetworkAvailable.value = false
    }
}