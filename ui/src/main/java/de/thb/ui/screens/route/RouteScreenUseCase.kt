package de.thb.ui.screens.route

import android.content.Context
import de.thb.core.domain.PlaceEntity

sealed class RouteScreenUseCase {
    data class RequestLocationUpdatesUseCase(
        val context: Context,
    ) : RouteScreenUseCase()

    data class OpenPlaceDetailsUseCase(
        val place: PlaceEntity,
    ) : RouteScreenUseCase()
}
