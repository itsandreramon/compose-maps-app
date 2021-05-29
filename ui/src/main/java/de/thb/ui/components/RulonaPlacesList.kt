package de.thb.ui.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.PlaceEntity

@Composable
fun RulonaPlacesList(places: List<PlaceEntity>, onItemClick: () -> Unit) {
    LazyColumn {
        items(places) { place ->
            RulonaPlaceItem(title = place.name, onClick = onItemClick)
        }
    }
}