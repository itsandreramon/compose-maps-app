package de.thb.ui.components.places

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallMade
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import de.thb.core.domain.place.PlaceTrend
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_green
import de.thb.ui.theme.rulona_red

@Composable
fun RulonaPlaceItem(
    title: String,
    isInEditMode: Boolean,
    placeTrend: PlaceTrend,
    onClick: () -> Unit,
    onRemoved: () -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(margin_medium),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier
                .clickable { onClick() }
                .padding(margin_medium)
                .fillMaxWidth()
        ) {
            Row(Modifier.height(margin_large)) {
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

                Text(text = title)
            }

            if (isInEditMode) {
                Image(
                    imageVector = Icons.Default.Close,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    contentDescription = "Remove Bookmark",
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .clickable { onRemoved() }
                )
            } else {
                Image(
                    imageVector = Icons.Default.ChevronRight,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    contentDescription = "Open Place Details",
                    modifier = Modifier.align(Alignment.CenterEnd)
                )
            }
        }
    }
}

@Composable
@Preview
fun RulonaPlaceItemPreview() {
    RulonaPlaceItem(
        title = "Berlin",
        isInEditMode = false,
        onClick = {},
        onRemoved = {},
        placeTrend = PlaceTrend.UP,
    )
}
