package de.thb.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import de.thb.ui.theme.rulona_material_blue_600

@Composable
fun RulonaPlacesHeader(
    onEditClicked: () -> Unit,
    onCloseClicked: () -> Unit,
    isInEditMode: Boolean = false,
) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Meine Orte",
            fontWeight = FontWeight.Bold,
        )

        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .height(32.dp)
                .clickable { if (isInEditMode) onCloseClicked() else onEditClicked() }
                .align(Alignment.CenterEnd)
        ) {
            if (isInEditMode) {
                Text(
                    text = "Fertig",
                    color = rulona_material_blue_600,
                    fontWeight = FontWeight.Bold,
                )
            } else {
                Image(
                    imageVector = Icons.Default.Edit,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    contentDescription = null,
                )
            }
        }
    }

    Divider(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    )
}