package com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import  com.robertconstantindinescu.my_doctor_app.R

import  com.robertconstantindinescu.my_doctor_app.databinding.PlaceItemLayoutBinding
import  com.robertconstantindinescu.my_doctor_app.interfaces.NearLocationInterface
import  com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.GooglePlaceModel

/**
 * Este adapter que hace de recycler view. Va a implementear la interfaz.
 * porque si nos fijamos en el layout algunos de los elementos son clicables. Esto
 * lo hemos definido con onClick en el elemento correspondiente a guardar y direccioenes.
 * porque podremos guardar y mostrar las direcciones.
 *
 *Con lo cual a la hora de crearse o inflarse el layout tenemos que poder hacer usao de la
 * interfaz con los metodos para ahcer as acciones de guardar y mostrar direcciones.
 */
class GooglePlaceAdapter(private val nearLocationInterface: NearLocationInterface) :
    RecyclerView.Adapter<GooglePlaceAdapter.ViewHolder>() {

    private var googlePlaceModels: List<GooglePlaceModel>? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        //inflas el layout con databinding.
        val binding: PlaceItemLayoutBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.place_item_layout, parent, false
        )

        return ViewHolder(binding)
    }

    //vista hija toma los datos o funte de datos
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (googlePlaceModels != null) {
            val placeModel = googlePlaceModels!![position]
            //binding de las variables. En este caso el modelo.
            holder.binding.googlePlaceModel = placeModel
            //aqui seteas la vairble nearLocationInterface, con la interfaz que pasamos
            //por construcor para que cuadno se llamae a alagun metodo de esa interfaz en algun elemento
            //con la etiqueta onclick, haga la respectiva funcion.
            holder.binding.listener = nearLocationInterface

            //una vez seteados todps etos datos resulta que en el layout con databinding pues ya
            //se van actualizando los cambios. con los datos especificos.
        }
    }

    override fun getItemCount(): Int {
        return if (googlePlaceModels != null) googlePlaceModels!!.size else 0
    }

    fun setGooglePlaces(googlePlaceModel: List<GooglePlaceModel>) {
        googlePlaceModels = googlePlaceModel
        notifyDataSetChanged()
    }

    //esta clase es la que nos va a sustentar o contener los elementos de la cita hija.
    //es decir que es la qeu contendrá la vista hija montada y que gracias a la clase que
    //se genra autoamticamente podremos acceder a todos los elementos de dentro del layout.
    class ViewHolder(val binding: PlaceItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)
}