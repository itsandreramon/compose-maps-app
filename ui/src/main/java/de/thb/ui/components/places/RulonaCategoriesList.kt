package de.thb.ui.components.places

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import de.thb.core.domain.category.CategoryEntity
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.type.EditState

fun LazyListScope.rulonaCategoriesList(
    categories: List<CategoryEntity>,
    title: String,
    editState: EditState = EditState.Done(),
    isEditable: Boolean = true,
    onEditStateChanged: (EditState) -> Unit,
    onRemoveClicked: (CategoryEntity) -> Unit = {},
    onAddClicked: (CategoryEntity) -> Unit = {},
) {
    item {
        RulonaHeaderEditable(
            title = title,
            isEditable = isEditable,
            editState = editState,
            onEditStateChanged = onEditStateChanged,
        )
    }

    items(categories) { category ->
        RulonaCategoryWithRules(
            categoryWithRules = Pair(category, listOf()), // we don't need the specific rules
            editState = editState,
            onItemRemoved = { onRemoveClicked(category) },
            onItemAdded = { onAddClicked(category) },
        )
    }
}
