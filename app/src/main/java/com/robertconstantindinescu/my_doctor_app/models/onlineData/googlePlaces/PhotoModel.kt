package com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel

import android.os.Parcelable
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PhotoModel(
    @field:Json(name = "height")

    val height: Int?=null,

    @field:Json(name = "html_attributions")

    val htmlAttributions: List<String>?=null,

    @field:Json(name = "photo_reference")

    val photoReference: String?=null,

    @field:Json(name = "width")

    val width: Int?=null
): Parcelable {
    companion object {

        @JvmStatic
        @BindingAdapter("loadImage")
        fun loadImage(view: ImageView, image: String?) {
            Glide.with(view.context).load(image).into(view)
        }
    }
}