package de.thb.ui.components.places

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.placeTrendFromInt
import de.thb.ui.type.EditState

@Composable
fun RulonaPlacesList(
    places: List<PlaceEntity>,
    editState: EditState,
    onItemClick: (PlaceEntity) -> Unit = {},
    onItemRemoved: (PlaceEntity) -> Unit = {},
) {
    LazyColumn {
        items(places) { place ->
            RulonaPlaceItem(
                title = place.name,
                placeTrend = placeTrendFromInt(place.trend),
                isInEditMode = editState.isInEditMode,
                onClick = { onItemClick(place) },
                onRemoved = { onItemRemoved(place) },
            )
        }
    }
}
