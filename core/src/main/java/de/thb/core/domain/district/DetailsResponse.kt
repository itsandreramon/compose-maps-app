package de.thb.core.domain.district

import com.squareup.moshi.Json
import de.thb.core.domain.district.types.AddressDetails

data class DetailsResponse(

    @Json(name = "address")
    val address: List<AddressDetails>?,

    @Json(name = "localname")
    val localname: String,
)