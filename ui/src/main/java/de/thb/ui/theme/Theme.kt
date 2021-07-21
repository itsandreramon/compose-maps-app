package de.thb.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val RulonaLightColors = lightColors(
    primary = rulona_primary_petrol,
    secondary = Color.Black,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

private val RulonaDarkColors = darkColors(
    primary = Color.Gray,
    secondary = Color.Gray,
    onPrimary = Color.White,
    onSecondary = Color.White,
)

@Composable
fun RulonaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (isSystemInDarkTheme()) {
            RulonaDarkColors
        } else {
            RulonaLightColors
        },
        typography = RulonaTypography,
        shapes = RulonaShapes,
        content = content
    )
}
