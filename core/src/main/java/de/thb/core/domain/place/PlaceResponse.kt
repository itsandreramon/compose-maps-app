package de.thb.core.domain.place

import com.squareup.moshi.Json

data class PlaceResponse(

    @Json(name = "id")
    val id: String,

    @Json(name = "name")
    val name: String,

    @Json(name = "type")
    val type: String,

    @Json(name = "incidence")
    val incidence: Double,

    @Json(name = "trend")
    val trend: Int,

    @Json(name = "website")
    val website: String?,

    @Json(name = "example")
    val example: Boolean,
)
