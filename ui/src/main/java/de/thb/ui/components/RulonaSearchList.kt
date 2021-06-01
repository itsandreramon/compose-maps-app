package de.thb.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.PlaceEntity

@Composable
fun RulonaSearchList(
    places: List<PlaceEntity>,
    onItemClick: () -> Unit,
    isInEditMode: Boolean = false
) {
    LazyColumn {
        items(places) { place ->
            RulonaPlaceItem(
                title = place.name,
                isInEditMode = isInEditMode,
                onClick = onItemClick
            )
        }
    }
}
