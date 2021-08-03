package de.thb.core.domain

enum class Severity(val value: String, val status: Int) {
    GREEN("green", 2),
    YELLOW("yellow", 1),
    RED("red", 0),
    UNKNOWN("unknown", -1),
}
