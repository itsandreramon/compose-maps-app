package de.thb.core.domain.route

data class RouteRequest(
    val origin: List<Double>,
    val destination: String,
    val returnType: String = "decoded"
)