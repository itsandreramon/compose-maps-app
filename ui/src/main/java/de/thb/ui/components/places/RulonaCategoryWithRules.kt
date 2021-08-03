package de.thb.ui.components.places

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.rule.RuleEntity
import de.thb.ui.theme.corner_size_medium
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.margin_small
import de.thb.ui.type.EditState
import de.thb.ui.util.color
import de.thb.ui.util.getCategorySeverityForRules
import de.thb.ui.util.state

// TODO Move callbacks into edit state
@Composable
fun RulonaCategoryWithRules(
    categoryWithRules: Pair<CategoryEntity, List<RuleEntity>>,
    modifier: Modifier = Modifier,
    editState: EditState = EditState.Done(),
    onItemRemoved: () -> Unit,
    onItemAdded: () -> Unit,
) {
    val category = categoryWithRules.first
    val rules = categoryWithRules.second

    var expanded by state { false }

    Box(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = margin_small)
            ) {
                Row(
                    modifier = Modifier.align(Alignment.CenterStart),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val categoryStatusColor = remember(categoryWithRules.second) {
                        getCategorySeverityForRules(categoryWithRules.second)
                    }

                    AnimatedVisibility(editState is EditState.Done) {
                        Image(
                            colorFilter = ColorFilter.tint(categoryStatusColor.color()),
                            imageVector = Icons.Filled.Circle,
                            contentDescription = "Severity Indicator",
                            modifier = Modifier
                                .padding(start = margin_medium)
                                .size(12.dp)
                        )
                    }

                    Spacer(modifier = Modifier.padding(end = margin_medium))

                    Text(text = category.name)
                }

                Surface(
                    shape = RoundedCornerShape(corner_size_medium),
                    modifier = Modifier.align(Alignment.CenterEnd),
                ) {
                    val rotation by animateFloatAsState(
                        if (expanded) -90f else 90f
                    )

                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .clickable {
                                when (editState) {
                                    is EditState.Done -> {
                                        expanded = !expanded
                                    }
                                    is EditState.Editing -> {
                                        onItemRemoved()
                                    }
                                    is EditState.Adding -> {
                                        onItemAdded()
                                    }
                                }
                            }
                            .padding(horizontal = margin_medium, vertical = margin_small),
                    ) {
                        when (editState) {
                            is EditState.Done -> {
                                Image(
                                    imageVector = Icons.Default.ChevronRight,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                                    contentDescription = null,
                                    modifier = Modifier.rotate(rotation)
                                )
                            }
                            is EditState.Editing -> {
                                Image(
                                    imageVector = Icons.Default.Close,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                                    contentDescription = null,
                                )
                            }
                            is EditState.Adding -> {
                                Image(
                                    imageVector = Icons.Default.Add,
                                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                                    contentDescription = null,
                                )
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(visible = expanded) {
                Box(Modifier.padding(bottom = margin_small, start = margin_medium)) {
                    for (rule in rules) {
                        Text(
                            text = rule.text,
                            modifier = Modifier.padding(bottom = margin_medium)
                        )
                    }
                }
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}
