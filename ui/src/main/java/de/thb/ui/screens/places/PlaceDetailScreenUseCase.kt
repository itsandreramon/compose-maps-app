package de.thb.ui.screens.places

import de.thb.ui.type.EditState

sealed class PlaceDetailScreenUseCase {
    data class EditFiltersUseCase(
        val editState: EditState,
    ) : PlaceDetailScreenUseCase()
}
