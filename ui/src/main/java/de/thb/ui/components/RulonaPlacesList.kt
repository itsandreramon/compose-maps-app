package de.thb.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.PlaceEntity
import de.thb.ui.type.EditState

@Composable
fun RulonaPlacesList(
    places: List<PlaceEntity>,
    editState: EditState,
    onItemClick: () -> Unit,
    onItemRemoved: (PlaceEntity) -> Unit,
) {
    LazyColumn {
        items(places) { place ->
            RulonaPlaceItem(
                title = place.name,
                isInEditMode = editState.isInEditMode,
                onClick = onItemClick,
                onRemoved = { onItemRemoved(place) },
            )
        }
    }
}
