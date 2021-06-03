package de.thb.ui.components.search

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import de.thb.ui.theme.height_list_header
import de.thb.ui.theme.margin_medium

@Composable
fun RulonaSearchHeader() {
    Box(
        modifier = Modifier
            .padding(horizontal = margin_medium)
            .height(height_list_header)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Letzte Suchen",
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(vertical = margin_medium)
        )

        Divider(
            Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
        )
    }
}