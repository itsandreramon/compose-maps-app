package de.thb.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun RulonaSearchHeader() {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Letzte Suchen",
            fontWeight = FontWeight.Bold,
        )
    }

    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}
