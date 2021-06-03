package de.thb.core.util

import java.time.Instant
import java.time.format.DateTimeParseException

fun nowUtc(): String {
    return Instant.now().toString()
}

fun fromUtc(utc: String): Instant? {
    return try {
        Instant.parse(utc)
    } catch (e: DateTimeParseException) {
        null
    }
}
