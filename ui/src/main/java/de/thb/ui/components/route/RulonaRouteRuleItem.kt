package de.thb.ui.components.route

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.rule.RuleEntity
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.margin_small
import de.thb.ui.theme.rulona_red
import de.thb.ui.util.state

@Composable
fun RulonaRouteRuleItem(
    categoryWithRules: Pair<CategoryEntity, List<RuleEntity>>
) {
    var expanded by state { false }
    val rotation by animateFloatAsState(
        if (expanded) -90f else 90f
    )

    Column(Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .clickable { expanded = !expanded }
                .fillMaxWidth()
                .padding(
                    start = margin_medium,
                    end = margin_small,
                    top = margin_medium,
                    bottom = margin_medium,
                )
        ) {
            // TODO Use rules for places in route
            // TODO Move to component
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

                Text(categoryWithRules.first.name)
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
            Box(Modifier.padding(bottom = margin_small, start = margin_medium)) {
                for (rule in categoryWithRules.second) {
                    Text(
                        text = rule.text,
                        modifier = Modifier.padding(bottom = margin_medium)
                    )
                }
            }
        }
    }
}
