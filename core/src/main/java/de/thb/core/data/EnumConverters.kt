package de.thb.core.data

import androidx.room.TypeConverter
import de.thb.core.domain.Severity
import de.thb.core.domain.place.PlaceType

class EnumConverters {

    @TypeConverter
    fun toSeverity(value: String) = enumValueOf<Severity>(value)

    @TypeConverter
    fun fromSeverity(value: Severity) = value.name

    @TypeConverter
    fun toPlaceType(value: String) = enumValueOf<PlaceType>(value)

    @TypeConverter
    fun fromPlaceType(value: PlaceType) = value.name
}
