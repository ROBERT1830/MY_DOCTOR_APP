package com.robertconstantindinescu.my_doctor_app.bindingAdapters

import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.robertconstantindinescu.my_doctor_app.R

class DoctorNotesBinding {

    companion object {


        var cancerImagesMap = hashMapOf<Int, String>()
        var doctorNotesMap = hashMapOf<Int, String>()

        @BindingAdapter("uri", "position")
        @JvmStatic
        fun getUriImage(imageView: ImageView, uri: String, position: Int) {
            cancerImagesMap[position] = uri
            Log.d("cancerImagesList", cancerImagesMap.toString())
        }

        /**
         * With this bindingAdapter we are able to add doctor notes to a mutableList
         * and every time the text is changed the text will be updated and added to that particular
         * position in the mutableList.
         */
        @BindingAdapter("app:textChangedListener")
        @JvmStatic
        fun getDoctorNote(editText: EditText, position:Int) {
            doctorNotesMap[position] = editText.rootView.resources
                .getString(R.string.default_text_doctor_note)
            Log.d("-->doctorNotesList", doctorNotesMap.toString())
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?,
                    start: Int,
                    count: Int,
                    after: Int
                ) {


                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    if(editText.text.trim().toString().isNotEmpty()){
                        doctorNotesMap[position] = editText.text.trim().toString()

                    }else{
                        doctorNotesMap[position] = editText.rootView.resources
                            .getString(R.string.default_text_doctor_note)
                    }
                    Log.d("-->doctorNotesList", doctorNotesMap.toString())


                }

                override fun afterTextChanged(s: Editable?) {
                    if (editText.text.trim().toString().isEmpty()){
                        doctorNotesMap[position] = editText.rootView.resources
                            .getString(R.string.default_text_doctor_note)
                    }


                }
            })
        }


    }
}