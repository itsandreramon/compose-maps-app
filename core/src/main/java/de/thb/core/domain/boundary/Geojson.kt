package de.thb.core.domain.boundary

import com.squareup.moshi.FromJson
import com.squareup.moshi.JsonDataException
import com.squareup.moshi.Moshi
import com.squareup.moshi.ToJson

sealed class Geojson(val type: String) {

    data class Point(
        val coordinates: List<Double>?,
    ) : Geojson("Point")

    data class LineString(
        val coordinates: List<List<Double>>?,
    ) : Geojson("LineString")

    data class Polygon(
        val coordinates: List<List<List<Double>>>?,
    ) : Geojson("Polygon")

    data class MultiPolygon(
        val coordinates: List<List<List<List<Double>>>>?,
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
            "Point" -> Geojson.Point(json.coordinates as? List<Double>)
            "LineString" -> Geojson.LineString(json.coordinates as? List<List<Double>>)
            "Polygon" -> Geojson.Polygon(json.coordinates as? List<List<List<Double>>>)
            "MultiPolygon" -> Geojson.MultiPolygon(json.coordinates as? List<List<List<List<Double>>>>)
            else -> throw JsonDataException("unknown type: ${json.type}")
        }
    }
}
