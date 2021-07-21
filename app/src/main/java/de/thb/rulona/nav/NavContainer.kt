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
                onPlaceClicked = { placeUuid ->
                    navController.navigate("place_details/$placeUuid")
                }
            )
        }

        composable(Screen.Route.route) {
            RouteScreen()
        }

        composable(
            route = Screen.PlaceDetails.route,
            arguments = listOf(navArgument("place_uuid") { type = NavType.StringType })
        ) { navBackStackEntry ->
            val placeUuid = navBackStackEntry.arguments?.getString("place_uuid")

            if (placeUuid != null) {
                PlaceDetailsScreen(
                    placeUuid = placeUuid,
                    onBackClicked = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}
