package de.thb.ui.util

import androidx.compose.ui.graphics.Color
import de.thb.core.domain.Severity
import de.thb.core.domain.rule.RuleEntity
import de.thb.ui.theme.rulona_green
import de.thb.ui.theme.rulona_red
import de.thb.ui.theme.rulona_yellow

fun Severity.color(): Color {
    return when (this) {
        Severity.UNKNOWN -> Color.Gray
        Severity.RED -> rulona_red
        Severity.YELLOW -> rulona_yellow
        Severity.GREEN -> rulona_green
    }
}

fun severityOfStatus(status: Int): Severity {
    return when (status) {
        0 -> Severity.RED
        1 -> Severity.YELLOW
        2 -> Severity.GREEN
        else -> Severity.UNKNOWN
    }
}

fun getCategorySeverityForRules(rules: List<RuleEntity>): Severity {
    if (rules.isEmpty()) return Severity.UNKNOWN
    var lowest = Severity.GREEN

    for (rule in rules) {
        if (rule.status < lowest.status) {
            lowest = severityOfStatus(rule.status)
        }
    }

    return lowest
}
