package de.thb.core.domain

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson
import de.thb.core.domain.route.type.Coordinate

sealed class Geojson(val type: String) {

    data class Point(
        val coordinates: Coordinate?,
    ) : Geojson("Point")

    data class LineString(
        val coordinates: List<Coordinate>?,
    ) : Geojson("LineString")

    data class Polygon(
        val coordinates: List<List<Coordinate>>?,
    ) : Geojson("Polygon")

    data class MultiPolygon(
        val coordinates: List<List<List<Coordinate>>>?,
    ) : Geojson("MultiPolygon")
}

@Suppress("UNCHECKED_CAST")
class GeojsonAdapter {

    @ToJson
    fun toJson(geojson: Geojson): String {
        return Moshi.Builder().build()
            .adapter(Geojson::class.java)
            .toJson(geojson)
    }

    @FromJson
    fun fromJson(json: GeojsonJson): Geojson {
        return when (json.type) {
            "Point" -> Geojson.Point(json.coordinates as? Coordinate)
            "LineString" -> Geojson.LineString(json.coordinates as? List<Coordinate>)
            "Polygon" -> Geojson.Polygon(json.coordinates as? List<List<Coordinate>>)
            "MultiPolygon" -> Geojson.MultiPolygon(json.coordinates as? List<List<List<Coordinate>>>)
            else -> throw JsonDataException("unknown type: ${json.type}")
        }
    }
}
