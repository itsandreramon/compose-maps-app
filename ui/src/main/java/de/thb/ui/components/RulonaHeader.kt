package de.thb.ui.components

import androidx.compose.runtime.Composable

@Composable
fun RulonaHeader(
    title: String,
) {
    RulonaHeaderEditable(
        title = title,
        isEditable = false,
    )
}
