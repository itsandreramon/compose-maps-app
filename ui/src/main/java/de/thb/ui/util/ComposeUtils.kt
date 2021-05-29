package de.thb.ui.util

import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisallowComposableCalls
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember

@Composable
inline fun <T> state(
    calculation: @DisallowComposableCalls () -> T
): MutableState<T> {
    return remember { mutableStateOf(calculation()) }
}