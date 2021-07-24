package de.thb.ui.components.places

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.rule.RuleWithCategoryEntity
import de.thb.core.util.RuleUtils
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.type.EditState

@Composable
fun RulonaRulesList(
    rules: List<RuleWithCategoryEntity>,
    title: String,
    editState: EditState = EditState.Done(),
    isEditable: Boolean = true,
    onEditStateChanged: (EditState) -> Unit,
    onRemoveClicked: (CategoryEntity) -> Unit = {},
    onAddClicked: (CategoryEntity) -> Unit = {},
) {
    val rulesWithCategoriesGrouped = remember(rules) {
        RuleUtils.groupRulesByCategory(rules)
    }

    LazyColumn {
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
}
