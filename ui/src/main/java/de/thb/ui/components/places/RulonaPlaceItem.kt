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
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.tooling.preview.Preview
import de.thb.core.domain.Trend
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_material_green_600
import de.thb.ui.theme.rulona_material_red_600

@Composable
fun RulonaPlaceItem(
    title: String,
    isInEditMode: Boolean,
    trend: Trend = Trend.UP,
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
                Text(text = title)

                Image(
                    imageVector = when (trend) {
                        Trend.UP -> Icons.Default.ArrowDropUp
                        Trend.DOWN -> Icons.Default.ArrowDropDown
                    },
                    colorFilter = when (trend) {
                        Trend.UP -> ColorFilter.tint(rulona_material_red_600)
                        Trend.DOWN -> ColorFilter.tint(rulona_material_green_600)
                    },
                    contentDescription = null
                )
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
    )
}
