package de.thb.ui.components.places

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.padding
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import de.thb.core.domain.place.PlaceTrend
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_green
import de.thb.ui.theme.rulona_red

@Composable
fun PlaceTrendIndicator(placeTrend: PlaceTrend) {
    Image(
        imageVector = Icons.Default.CallMade,
        colorFilter = when (placeTrend) {
            PlaceTrend.UP -> ColorFilter.tint(rulona_red)
            PlaceTrend.NEUTRAL -> ColorFilter.tint(
                MaterialTheme.colors.onBackground.copy(
                    alpha = 0.3f
                )
            )
            PlaceTrend.DOWN -> ColorFilter.tint(rulona_green)
        },
        contentDescription = null,
        modifier = Modifier
            .padding(end = margin_medium)
            .rotate(
                degrees = when (placeTrend) {
                    PlaceTrend.UP -> 0f
                    PlaceTrend.NEUTRAL -> 45f
                    PlaceTrend.DOWN -> 90f
                }
            )
    )
}