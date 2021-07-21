package de.thb.ui.components.places

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.category.CategoryEntity
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.type.EditState

@Composable
fun RulonaFilterList(
    categories: List<CategoryEntity>,
    title: String,
    editState: EditState = EditState.Done(),
    isEditable: Boolean = true,
    onEditStateChanged: (EditState) -> Unit,
    onRemoveClicked: (CategoryEntity) -> Unit = {},
    onAddClicked: (CategoryEntity) -> Unit = {},
) {
    LazyColumn {
        item {
            RulonaHeaderEditable(
                title = title,
                isEditable = isEditable,
                editState = editState,
                onEditStateChanged = onEditStateChanged,
            )
        }

        items(categories) { filter ->
            RulonaFilter(
                category = filter,
                editState = editState,
                onItemRemoved = { onRemoveClicked(filter) },
                onItemAdded = { onAddClicked(filter) },
            )
        }
    }
}
