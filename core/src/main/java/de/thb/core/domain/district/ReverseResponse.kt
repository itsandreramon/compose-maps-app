package de.thb.core.domain.district

import com.squareup.moshi.Json
import de.thb.core.domain.district.types.AddressReverse

data class ReverseResponse(

    @Json(name = "osm_id")
    val osm_id: Int,

    @Json(name = "osm_type")
    val osm_type: String,

    @Json(name = "address")
    val address: AddressReverse,
)