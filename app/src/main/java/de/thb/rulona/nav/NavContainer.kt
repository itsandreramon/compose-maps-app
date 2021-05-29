package de.thb.rulona.nav

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Directions
import androidx.compose.material.icons.filled.Explore
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import de.thb.ui.screens.places.PlacesScreen
import de.thb.ui.screens.route.RouteScreen

@Composable
fun NavContainer(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Places.route) {
        composable(Screen.Places.route) {
            PlacesScreen()
        }

        composable(Screen.Route.route) {
            RouteScreen()
        }
    }
}

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Route : Screen("route", "Route", Icons.Filled.Explore)
    object Places : Screen("places", "Places", Icons.Filled.Directions)
}
