package de.thb.ui.screens.route

import android.content.Context

sealed class RouteScreenUseCase {
    data class RequestLocationUpdates(
        val context: Context,
    ) : RouteScreenUseCase()
}
