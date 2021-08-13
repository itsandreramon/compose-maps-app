package de.thb.ui.theme

import androidx.compose.material.MaterialTheme
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RulonaLightColors = lightColors(
    primary = rulona_primary_petrol,
    secondary = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

@Composable
fun RulonaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = RulonaLightColors,
        typography = RulonaTypography,
        shapes = RulonaShapes,
        content = content
    )
}
