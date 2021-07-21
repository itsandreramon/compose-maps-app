package de.thb.ui.components.search

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import de.thb.core.domain.place.PlaceEntity

@Composable
fun RulonaSearchList(
    places: List<PlaceEntity>,
    onItemClick: (PlaceEntity) -> Unit,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
) {
    LazyColumn {
        // use key = ... as a workaround to correctly recompose
        // see: https://issuetracker.google.com/issues/189971666
        items(places, key = { it.toString() }) { place ->
            RulonaSearchItem(
                title = place.name,
                isBookmarked = place.isBookmarked,
                onClick = { onItemClick(place) },
                onBookmarkClicked = { onItemBookmarkClicked(place) },
            )
        }
    }
}
