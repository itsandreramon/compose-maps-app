package de.thb.core.domain.rule

import com.squareup.moshi.Json

data class RuleReponse(

    @Json(name = "id")
    val id: Int,

    @Json(name = "categoryId")
    val categoryId: Long,

    @Json(name = "status")
    val status: Int,

    @Json(name = "restriction")
    val restriction: Int,

    @Json(name = "text")
    val text: String,

    @Json(name = "timestamp")
    val timestamp: String,
)
