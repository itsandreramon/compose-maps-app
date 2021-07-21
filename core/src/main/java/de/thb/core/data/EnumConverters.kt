package de.thb.core.data

import androidx.room.TypeConverter
import de.thb.core.domain.place.PlaceType
import de.thb.core.domain.place.placeTypeFromString

class EnumConverters {

    @TypeConverter
    fun toPlaceType(value: String) = placeTypeFromString(value)

    @TypeConverter
    fun fromPlaceType(value: PlaceType) = value.name
}
