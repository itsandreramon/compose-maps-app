package de.thb.ui.util

import androidx.compose.runtime.*

@Composable
inline fun <T> state(
    calculation: @DisallowComposableCalls () -> T
): MutableState<T> {
    return remember { mutableStateOf(calculation()) }
}