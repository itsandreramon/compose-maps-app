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
    editState: EditState = EditState.Done(),
    isEditable: Boolean = true,
    onEditStateChanged: (EditState) -> Unit,
) {
    LazyColumn {
        item {
            RulonaHeaderEditable(
                title = "Mein Filter",
                isEditable = isEditable,
                editState = editState,
                onEditStateChanged = onEditStateChanged
            )
        }

        items(filters) { filter ->
            RulonaFilter(
                filter = filter,
                editState = editState,
                onItemRemoved = {}
            )
        }
    }
}
