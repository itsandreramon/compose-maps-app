package de.thb.core.domain

import com.squareup.moshi.Json

data class ExampleResponse(

    @Json(name = "id")
    val id: Long = 0,

    @Json(name = "name")
    val name: String = "",
)
