package com.robertconstantindinescu.my_doctor_app.models.googlePlaceModel



import android.os.Parcelable
import com.google.firebase.database.annotations.Nullable
import com.squareup.moshi.Json
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

@Parcelize
data class GooglePlaceModel(
    @field:Json(name = "business_status")
    val businessStatus: String?=null,

    @field:Json(name = "geometry")

    val geometry: GeometryModel?=null,

    @field:Json(name = "icon")

    val icon: String?=null,

    @field:Json(name = "name")

    val name: String?=null,

    @field:Json(name = "obfuscated_type")

    val obfuscatedType: @RawValue List<Any>?=null,

    @field:Json(name = "photos")

    val photos: List<PhotoModel>?=null,

    @field:Json(name = "place_id")

    val placeId: String?=null,

    @field:Json(name = "rating")

    val rating: Double?=null,

    @field:Json(name = "reference")

    val reference: String?=null,

    @field:Json(name = "scope")
    val scope: String?=null,

    @field:Json(name = "types")
    val types: List<String>?=null,

    @field:Json(name = "user_ratings_total")
    val userRatingsTotal: Int?=null,

    @field:Json(name = "vicinity")
    val vicinity: String?=null,

    //esto indica que es un objeto transiente que no va a formar parte del objeto serializado
    @Transient
    var saved: Boolean?=null
): Parcelable