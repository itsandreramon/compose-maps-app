package de.thb.ui.components

import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun ScreenTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        style = MaterialTheme.typography.h4,
        text = title,
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun ScreenTitlePreview() {
    ScreenTitle(title = "Home")
}
