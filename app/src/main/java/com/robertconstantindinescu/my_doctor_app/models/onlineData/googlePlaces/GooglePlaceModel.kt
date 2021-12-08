package com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel



import android.os.Parcelable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize

data class GooglePlaceModel(
    @field:Json(name = "business_status")
    val businessStatus: String?,

    @field:Json(name = "geometry")

    val geometry: GeometryModel?,

    @field:Json(name = "icon")

    val icon: String?,

    @field:Json(name = "name")

    val name: String?,

    @field:Json(name = "obfuscated_type")

    val obfuscatedType: @RawValue List<Any>?,

    @field:Json(name = "photos")

    val photos: List<PhotoModel>?,

    @field:Json(name = "place_id")

    val placeId: String?,

    @field:Json(name = "rating")

    val rating: Double?,

    @field:Json(name = "reference")

    val reference: String?,

    @field:Json(name = "scope")
    val scope: String?,

    @field:Json(name = "types")
    val types: List<String>?,

    @field:Json(name = "user_ratings_total")
    val userRatingsTotal: Int?,

    @field:Json(name = "vicinity")
    val vicinity: String?,

    //esto indica que es un objeto transiente que no va a formar parte del objeto serializado
    @Transient
    var saved: Boolean?
): Parcelable