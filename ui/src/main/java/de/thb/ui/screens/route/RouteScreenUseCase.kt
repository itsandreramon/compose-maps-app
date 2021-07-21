package de.thb.ui.screens.route

import android.content.Context
import de.thb.core.domain.place.PlaceEntity
import de.thb.ui.type.SearchState

sealed class RouteScreenUseCase {
    data class RequestLocationUpdatesUseCase(
        val context: Context,
    ) : RouteScreenUseCase()

    data class OpenPlaceDetailsUseCase(
        val place: PlaceEntity,
    ) : RouteScreenUseCase()

    data class SearchUseCase(
        val searchState: SearchState,
    ) : RouteScreenUseCase()
}
