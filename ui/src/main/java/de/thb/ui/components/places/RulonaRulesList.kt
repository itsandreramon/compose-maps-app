package de.thb.ui.components.places

import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.rule.RuleEntity
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.type.EditState

fun LazyListScope.rulonaRulesList(
    rulesWithCategoriesGrouped: List<Pair<CategoryEntity, List<RuleEntity>>>,
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

    items(rulesWithCategoriesGrouped) { categoryWithRules ->
        RulonaCategoryWithRules(
            categoryWithRules = categoryWithRules,
            editState = editState,
            onItemRemoved = { onRemoveClicked(categoryWithRules.first) },
            onItemAdded = { onAddClicked(categoryWithRules.first) },
        )
    }
}
