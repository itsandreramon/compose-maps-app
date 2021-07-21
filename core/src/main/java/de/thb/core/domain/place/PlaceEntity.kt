package de.thb.core.domain.place

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "places")
data class PlaceEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: String,

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "type")
    val type: PlaceType,

    @ColumnInfo(name = "incidence")
    val incidence: Double,

    @ColumnInfo(name = "trend")
    val trend: PlaceTrend = PlaceTrend.NEUTRAL,

    @ColumnInfo(name = "website")
    val website: String,

    @ColumnInfo(name = "example")
    val example: Boolean = true,

    @ColumnInfo(name = "is_bookmarked")
    val isBookmarked: Boolean = false,

    @ColumnInfo(name = "searched_at_utc")
    val searchedAtUtc: String? = null,
)
