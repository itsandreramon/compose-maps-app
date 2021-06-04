package de.thb.ui.components.places

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import de.thb.ui.theme.corner_size_medium
import de.thb.ui.theme.height_list_header
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.margin_small
import de.thb.ui.theme.rulona_material_blue_600
import de.thb.ui.type.EditState
import de.thb.ui.type.toggleEditState

@Composable
fun RulonaPlacesHeader(
    editState: EditState,
    onEditStateChanged: (EditState) -> Unit,
) {

    Box(
        modifier = Modifier
            .height(height_list_header)
            .fillMaxWidth(),
    ) {
        Text(
            text = "Meine Orte",
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .padding(vertical = margin_medium)
                .padding(start = margin_medium),
        )

        Surface(
            shape = RoundedCornerShape(corner_size_medium),
            modifier = Modifier.align(Alignment.CenterEnd),
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .clickable { onEditStateChanged(toggleEditState(editState)) }
                    .padding(horizontal = margin_medium, vertical = margin_small)
                    .align(Alignment.CenterEnd)
            ) {
                if (editState is EditState.Editing) {
                    Text(
                        text = "Fertig",
                        color = rulona_material_blue_600,
                        fontWeight = FontWeight.Bold,
                    )
                } else {
                    Image(
                        imageVector = Icons.Default.Edit,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                        contentDescription = "Edit Places",
                    )
                }
            }
        }

        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = margin_small)
                .align(Alignment.BottomCenter)
        )
    }
}
