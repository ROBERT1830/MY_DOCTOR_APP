package com.robertconstantindinescu.my_doctor_app.adapters.mapsAdapters

import androidx.recyclerview.widget.RecyclerView

import android.view.ViewGroup
import android.view.LayoutInflater
import android.os.Build
import android.text.Html
import com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel.directionPlaceModel.DirectionStepModel
import com.robertconstantindinescu.my_doctor_app.databinding.StepItemLayoutBinding

class DirectionStepAdapter : RecyclerView.Adapter<DirectionStepAdapter.ViewHolder>() {
    private var directionStepModels: List<DirectionStepModel>? = null
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = StepItemLayoutBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )
        //devolvemos la vissta hija creada con los datos pegados.
        return ViewHolder(binding)
    }

    //pegamos la info
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (directionStepModels != null) {
            //with that is very impressive becasue we will get the specific data from
            //each and every property of the directionStepModels object.
            val (distance, duration, _, htmlInstructions) = directionStepModels!![position]
            //si el sdk es mayou o igual a 24
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.binding.txtStepHtml.text =
                    //the class Html process html string into displayable text.
                    //fromHtml coge el html del objeto y lo convierte a algo legible.
                    Html.fromHtml(htmlInstructions, Html.FROM_HTML_MODE_LEGACY)
            } else {
                holder.binding.txtStepHtml.text = Html.fromHtml(htmlInstructions)
            }
            holder.binding.txtStepTime.text = duration!!.text
            holder.binding.txtStepDistance.text = distance!!.text
        }
    }

    override fun getItemCount(): Int {
        return if (directionStepModels != null) directionStepModels!!.size else 0
    }

    fun setDirectionStepModels(directionStepModels: List<DirectionStepModel>?) {
        this.directionStepModels = directionStepModels
        notifyDataSetChanged()
    }

    //aqui haces el binding con la clase que se genera autoamticametne apra acfeder a todos los elementos de
    //ese layout y ademas tenemos qeu estaclase devolvera ese viewholder, la raiz pero dentro del recyclerview.
    inner class ViewHolder(val binding: StepItemLayoutBinding) : RecyclerView.ViewHolder(
        binding.root
    )
}