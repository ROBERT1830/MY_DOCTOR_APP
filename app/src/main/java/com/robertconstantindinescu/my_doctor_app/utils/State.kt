package com.robertconstantindinescu.my_doctor_app.utils

import java.lang.Error

sealed class State<T> {
    class Loading<T>(val flag: T): State<T>()
    class Succes<T>(val data: T):State<T>()
    class Failed<T>(val error: String): State<T>();

    companion object{
        fun <T> loading(flag: T) = Loading(flag)
        fun <T> succes(data: T) = Succes(data)
        fun <T> failed(error:String) = Failed<T>(error)

    }
}