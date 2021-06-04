package de.thb.ui.util

import androidx.compose.ui.graphics.Color
import de.thb.core.domain.Severity
import de.thb.ui.theme.rulona_material_green_600
import de.thb.ui.theme.rulona_material_red_600
import de.thb.ui.theme.rulona_material_yellow_600

fun Severity.color(): Color {
    return when (this) {
        Severity.RED -> rulona_material_red_600
        Severity.YELLOW -> rulona_material_yellow_600
        Severity.GREEN -> rulona_material_green_600
    }
}
