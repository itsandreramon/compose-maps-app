package de.thb.ui.components.search

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.PlaceEntity

@Composable
fun RulonaSearchList(
    places: List<PlaceEntity>,
    onItemClick: (PlaceEntity) -> Unit,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
) {
    LazyColumn {
        items(places) { place ->
            RulonaSearchItem(
                title = place.name,
                isBookmarked = place.isBookmarked,
                onClick = { onItemClick(place) },
                onBookmarkClicked = { onItemBookmarkClicked(place) },
            )
        }
    }
}
