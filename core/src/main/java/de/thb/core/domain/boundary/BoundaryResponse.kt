package de.thb.core.domain.boundary

import de.thb.core.domain.Geojson

data class BoundaryResponse(
    val boundingbox: List<String>,
    val `class`: String,
    val display_name: String,
    val geojson: Geojson,
    val icon: String,
    val importance: Double,
    val lat: String,
    val licence: String,
    val lon: String,
    val osm_id: Long,
    val osm_type: String,
    val place_id: Int,
    val type: String
)
