package de.thb.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import com.google.accompanist.systemuicontroller.rememberSystemUiController

@Composable
fun setStatusBarIconColorInSideEffect(darkIcons: Boolean = true) {
    val systemUiController = rememberSystemUiController()

    SideEffect {
        systemUiController.setStatusBarColor(
            darkIcons = darkIcons,
            color = Color.Transparent,
        )
    }
}
