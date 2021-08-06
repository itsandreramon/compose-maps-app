package de.thb.core.domain.district.types

import com.squareup.moshi.Json

data class AddressReverse(

    @Json(name = "borough")
    val borough: String,

    @Json(name = "city")
    val city: String,

    @Json(name = "country")
    val country: String,

    @Json(name = "country_code")
    val country_code: String,

    @Json(name = "neighbourhood")
    val neighbourhood: String,

    @Json(name = "postcode")
    val postcode: String,

    @Json(name = "road")
    val road: String,

    @Json(name = "state")
    val state: String,

    @Json(name = "suburb")
    val suburb: String
)