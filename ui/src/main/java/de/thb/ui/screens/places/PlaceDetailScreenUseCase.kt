package de.thb.ui.screens.places

import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.place.PlaceEntity
import de.thb.ui.type.EditState

sealed class PlaceDetailScreenUseCase {
    data class EditCategoriesUseCase(
        val editState: EditState,
    ) : PlaceDetailScreenUseCase()

    data class RemoveCategoryUseCase(
        val category: CategoryEntity,
    ) : PlaceDetailScreenUseCase()

    data class AddCategoryUseCase(
        val category: CategoryEntity,
    ) : PlaceDetailScreenUseCase()

    data class BookmarkPlaceUseCase(
        val place: PlaceEntity
    ) : PlaceDetailScreenUseCase()
}
