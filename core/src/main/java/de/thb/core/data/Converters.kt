package de.thb.core.data

import androidx.room.TypeConverter
import de.thb.core.domain.Severity

class Converters {

    @TypeConverter
    fun toSeverity(value: Int) = enumValues<Severity>()[value]

    @TypeConverter
    fun fromSeverity(value: Severity) = value.ordinal
}
