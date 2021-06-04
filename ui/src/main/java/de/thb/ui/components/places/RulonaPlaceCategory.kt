package de.thb.ui.components.places

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import de.thb.core.domain.Severity
import de.thb.ui.theme.corner_size_medium
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.margin_small
import de.thb.ui.util.color
import de.thb.ui.util.state

@Composable
fun RulonaPlaceCategory(name: String, severity: Severity, modifier: Modifier = Modifier) {
    var expanded by state { false }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter)
                .padding(start = margin_medium)
                .padding(vertical = margin_small)
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.align(Alignment.CenterStart)) {
                    Image(
                        colorFilter = ColorFilter.tint(severity.color()),
                        imageVector = Icons.Filled.Circle,
                        contentDescription = "Severity Indicator",
                        modifier = Modifier
                            .size(16.dp)
                            .align(Alignment.CenterVertically)
                    )
                    Text(
                        text = name,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = margin_medium)
                    )
                }

                Surface(
                    shape = RoundedCornerShape(corner_size_medium),
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    val rotation by animateFloatAsState(targetValue = if (expanded) -90f else 90f)

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable { expanded = !expanded }
                            .padding(horizontal = margin_medium, vertical = margin_small),
                    ) {
                        Image(
                            imageVector = Icons.Default.ChevronRight,
                            contentDescription = null,
                            modifier = Modifier.rotate(rotation)
                        )
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Box {
                    Text(text = "This is some text that is only visible after the user expanded the card.")
                }
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = margin_small)
                .align(Alignment.BottomCenter)
        )
    }
}