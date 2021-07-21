package de.thb.ui.screens.places

import de.thb.core.domain.category.CategoryEntity
import de.thb.ui.type.EditState

sealed class PlaceDetailScreenUseCase {
    data class EditFiltersUseCase(
        val editState: EditState,
    ) : PlaceDetailScreenUseCase()

    data class RemoveFilterUseCase(
        val category: CategoryEntity,
    ) : PlaceDetailScreenUseCase()

    data class AddFilterUseCase(
        val category: CategoryEntity,
    ) : PlaceDetailScreenUseCase()
}
