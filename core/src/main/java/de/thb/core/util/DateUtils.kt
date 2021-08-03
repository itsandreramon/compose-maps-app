package de.thb.core.util

import android.util.Log
import java.time.Instant
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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

fun dateFromTimestamp(timestamp: String?, pattern: String = "yyyyMMdd"): LocalDate? {
    return if (timestamp != null) {
        try {
            LocalDate.parse(timestamp, DateTimeFormatter.ofPattern(pattern))
        } catch (e: DateTimeParseException) {
            Log.e("exception", "${e.message}")
            null
        }
    } else {
        null
    }
}

fun dateToString(date: LocalDate?, pattern: String = "dd.MM.yyyy"): String? {
    return if (date != null) {
        try {
            date.format(DateTimeFormatter.ofPattern(pattern))
        } catch (e: IllegalArgumentException) {
            null
        }
    } else {
        null
    }
}
