package de.thb.ui.screens.places

import de.thb.core.domain.FilterEntity
import de.thb.ui.type.EditState

sealed class PlaceDetailScreenUseCase {
    data class EditFiltersUseCase(
        val editState: EditState,
    ) : PlaceDetailScreenUseCase()

    data class RemoveFilterUseCase(
        val filter: FilterEntity,
    ) : PlaceDetailScreenUseCase()

    data class AddFilterUseCase(
        val filter: FilterEntity,
    ) : PlaceDetailScreenUseCase()
}
