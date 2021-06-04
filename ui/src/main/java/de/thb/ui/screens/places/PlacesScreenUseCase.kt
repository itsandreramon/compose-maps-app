package de.thb.ui.screens.places

import de.thb.core.domain.PlaceEntity
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState

sealed class PlacesScreenUseCase {
    data class EditBookmarks(
        val editState: EditState,
    ) : PlacesScreenUseCase()

    data class Search(
        val searchState: SearchState
    ) : PlacesScreenUseCase()

    data class TogglePlaceBookmark(
        val place: PlaceEntity
    ) : PlacesScreenUseCase()

    data class SetPlaceSearchTimestamp(
        val place: PlaceEntity
    ) : PlacesScreenUseCase()
}
