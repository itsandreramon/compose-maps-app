package de.thb.ui.screens.one

import android.content.Context

sealed class ScreenOneUseCase {
    data class RequestLocationUpdates(
        val context: Context,
    ) : ScreenOneUseCase()
}
