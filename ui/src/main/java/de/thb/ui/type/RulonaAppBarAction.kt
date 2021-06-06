package de.thb.ui.type

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.graphics.vector.ImageVector

sealed class RulonaAppBarAction(
    val icon: ImageVector,
    val contentDescription: String?,
    val onClick: () -> Unit,
) {
    class Back(onClick: () -> Unit) : RulonaAppBarAction(
        icon = Icons.Filled.ChevronLeft,
        contentDescription = "Back",
        onClick = onClick,
    )

    class Notify(onClick: () -> Unit) : RulonaAppBarAction(
        icon = Icons.Filled.Notifications,
        contentDescription = null,
        onClick = onClick,
    )

    class Share(onClick: () -> Unit) : RulonaAppBarAction(
        icon = Icons.Filled.Share,
        contentDescription = null,
        onClick = onClick,
    )
}
