package de.thb.ui.screens.places

import android.content.Context
import de.thb.core.domain.place.PlaceEntity
import de.thb.ui.type.DialogType
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState

sealed class PlacesScreenUseCase {
    data class EditBookmarksUseCase(
        val editState: EditState,
    ) : PlacesScreenUseCase()

    data class SearchUseCase(
        val searchState: SearchState
    ) : PlacesScreenUseCase()

    data class SearchCurrentLocationUseCase(
        val context: Context,
    ) : PlacesScreenUseCase()

    data class TogglePlaceBookmarkUseCase(
        val place: PlaceEntity
    ) : PlacesScreenUseCase()

    data class SetPlaceSearchTimestampUseCase(
        val place: PlaceEntity
    ) : PlacesScreenUseCase()

    data class HideDialogUseCase(
        val dialogType: DialogType,
    ) : PlacesScreenUseCase()
}
