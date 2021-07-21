package de.thb.core.domain.category

import com.squareup.moshi.Json

data class CategoryResponse(

    @Json(name = "id")
    val id: Int,

    @Json(name = "name")
    val name: String,
)
