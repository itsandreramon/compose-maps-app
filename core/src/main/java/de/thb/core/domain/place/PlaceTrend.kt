package de.thb.core.domain.place

enum class PlaceTrend {
    UP, DOWN, NEUTRAL
}

fun placeTrendFromInt(trendInt: Int): PlaceTrend {
    return when (trendInt) {
        1 -> PlaceTrend.UP
        -1 -> PlaceTrend.DOWN
        else -> PlaceTrend.NEUTRAL
    }
}
