package de.thb.rulona.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import de.thb.ui.screens.Screen
import de.thb.ui.screens.places.PlaceDetailsScreen
import de.thb.ui.screens.places.PlacesScreen
import de.thb.ui.screens.route.RouteScreen

@Composable
fun NavContainer(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.Places.route) {
        composable(Screen.Places.route) {
            PlacesScreen(
                onPlaceLoaded = { placeId ->
                    navController.navigate("place_details/$placeId")
                }
            )
        }

        composable(Screen.Route.route) {
            RouteScreen()
        }

        composable(
            route = Screen.PlaceDetails.route,
            arguments = listOf(navArgument("place_id") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val placeId = navBackStackEntry.arguments?.getString("place_id")

            if (placeId != null) {
                PlaceDetailsScreen(
                    placeId = placeId,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
