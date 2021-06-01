package de.thb.core.domain

import com.squareup.moshi.Json

data class PlaceResponse(

    @Json(name = "id")
    val uuid: String,

    @Json(name = "title")
    val name: String = "",
)
