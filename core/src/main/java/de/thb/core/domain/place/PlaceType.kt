package de.thb.core.domain.place

enum class PlaceType(val value: String) {
    STAAT("staat"),
    BUNDESLAND("bundesland"),
    LANDKREIS("landkreis"),
    UNDEFINED("undefined")
}

fun placeTypeFromString(value: String): PlaceType {
    return PlaceType.values()
        .firstOrNull { it.value.equals(value, ignoreCase = true) }
        ?: PlaceType.UNDEFINED
}
