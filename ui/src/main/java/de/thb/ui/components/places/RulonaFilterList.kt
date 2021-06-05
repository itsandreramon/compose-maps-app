package de.thb.ui.components.places

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.FilterEntity
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.type.EditState

@Composable
fun RulonaFilterList(
    filters: List<FilterEntity>,
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

        items(filters) { filter ->
            RulonaFilter(
                filter = filter,
                onItemRemoved = {}
            )
        }
    }
}
