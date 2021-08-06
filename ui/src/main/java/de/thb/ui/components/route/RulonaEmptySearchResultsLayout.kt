package de.thb.ui.components.places

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import de.thb.ui.theme.margin_large

@Composable
fun RulonaEmptySearchResultsLayout() {
    Box(Modifier.fillMaxSize()) {
        Text(
            text = "Es konnten keine Orte gefunden werden.",
            textAlign = TextAlign.Center,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(margin_large)
        )
    }
}