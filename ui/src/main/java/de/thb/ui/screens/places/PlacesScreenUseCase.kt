package de.thb.ui.screens.places

import de.thb.core.domain.place.PlaceEntity
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState

sealed class PlacesScreenUseCase {
    data class EditBookmarksUseCase(
        val editState: EditState,
    ) : PlacesScreenUseCase()

    data class SearchUseCase(
        val searchState: SearchState
    ) : PlacesScreenUseCase()

    object SearchCurrentLocationUseCase : PlacesScreenUseCase()

    data class TogglePlaceBookmarkUseCase(
        val place: PlaceEntity
    ) : PlacesScreenUseCase()

    data class SetPlaceSearchTimestampUseCase(
        val place: PlaceEntity
    ) : PlacesScreenUseCase()
}
