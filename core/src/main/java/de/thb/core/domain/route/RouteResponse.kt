package de.thb.core.domain.route

import de.thb.core.domain.route.type.Coordinate
import de.thb.core.domain.route.type.RestrictedPlace

data class RouteResponse(
    val restrictedPlaces: List<RestrictedPlace>,
    val route: List<List<Coordinate>>,
)