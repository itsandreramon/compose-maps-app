package de.thb.ui.components.route

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.rule.RuleEntity
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_background_light
import de.thb.ui.theme.rulona_red
import de.thb.ui.util.state

@Composable
fun RulonaRouteRuleItem(placeWithRules: Pair<PlaceEntity, List<RuleEntity>>) {
    var expanded by state { false }
    val rotation by animateFloatAsState(
        if (expanded) -90f else 90f
    )

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .padding(margin_medium)
        ) {
            Row(
                Modifier
                    .weight(0.9f)
                    .fillMaxWidth()
            ) {
                Image(
                    imageVector = Icons.Default.Warning,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(rulona_red),
                    modifier = Modifier.padding(end = margin_medium)
                )

                Text(placeWithRules.first.name)
            }

            Image(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                modifier = Modifier
                    .rotate(rotation)
                    .weight(0.1f)
            )
        }

        AnimatedVisibility(visible = expanded) {
            Column(Modifier.background(rulona_background_light)) {
                for (rule in placeWithRules.second) {
                    Text(
                        text = rule.text,
                        modifier = Modifier
                            .padding(margin_medium)
                    )
                }
            }

            Divider(Modifier.fillMaxWidth())
        }
    }

    Divider(Modifier.fillMaxWidth())
}
