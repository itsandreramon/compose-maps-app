package de.thb.core.domain.place

import com.squareup.moshi.Json

enum class PlaceType(val value: String) {

    @Json(name = "staat")
    STAAT("staat"),

    @Json(name = "bundesland")
    BUNDESLAND("bundesland"),

    @Json(name = "landkreis")
    LANDKREIS("landkreis")
}
