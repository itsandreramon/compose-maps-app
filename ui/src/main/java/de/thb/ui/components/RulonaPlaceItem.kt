package de.thb.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import de.thb.core.domain.Trend
import de.thb.ui.theme.rulona_material_green_600
import de.thb.ui.theme.rulona_material_red_600

@Composable
fun RulonaPlaceItem(
    title: String,
    onClick: () -> Unit,
    isInEditMode: Boolean,
    trend: Trend = Trend.UP,
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier
                .clickable { onClick() }
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Row {
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

            Image(
                imageVector = if (isInEditMode) {
                    Icons.Default.Close
                } else {
                    Icons.Default.ChevronRight
                },
                colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                contentDescription = null,
                modifier = Modifier.align(Alignment.CenterEnd)
            )
        }
    }
}

@Composable
@Preview
fun RulonaPlaceItemPreview() {
    RulonaPlaceItem("Berlin", onClick = {}, isInEditMode = false)
}
