package com.robertconstantindinescu.my_doctor_app.utils

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.robertconstantindinescu.my_doctor_app.utils.Constants.Companion.STORAGE_REQUEST_CODE


class AppPermissions {
    fun isStorageOk(context: Context): Boolean {
        //compruebas si hay se han aceptado para leer el almacenamiento externo.
        /*checkSelfPermission devolvera granted(true) si se los permisos están aceptados. */
        return ContextCompat.checkSelfPermission( //ContextCompat nos permite acceder  los recuross del contexto de la palciacion.
            context,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun requestStoragePermission(activity: Activity) {
        //pedir permisos al usuario.
        ActivityCompat.requestPermissions(
            activity,
            arrayOf(
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ),
            Constants.STORAGE_REQUEST_CODE
        )
    }
}