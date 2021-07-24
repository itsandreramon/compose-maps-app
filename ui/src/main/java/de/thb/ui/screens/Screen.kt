package de.thb.ui.screens

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Search
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector?) {
    object Route : Screen("route", "Route", Icons.Filled.Directions)
    object Places : Screen("places", "Orte", Icons.Filled.Search)
    object PlaceDetails : Screen("place_details/{place_uuid}", "Place Details", null)
}
