package de.thb.ui.components.places

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.Filter
import de.thb.core.domain.Severity
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.type.EditState

@Composable
fun RulonaFilterList(
    filters: List<Filter>,
    onEditStateChanged: (EditState) -> Unit,
) {
    LazyColumn {
        item {
            RulonaHeaderEditable(
                title = "Mein Filter",
                editState = EditState.Done(),
                onEditStateChanged = onEditStateChanged
            )
        }

        items(filters) {
            RulonaFilter(
                filter = Filter("Restaurants", Severity.RED),
                onItemRemoved = {}
            )
        }
    }
}