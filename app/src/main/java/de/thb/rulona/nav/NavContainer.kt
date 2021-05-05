package de.thb.rulona.nav

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigate
import de.thb.ui.screens.ScreenOne
import de.thb.ui.screens.ScreenTwo

@Composable
fun NavContainer(navController: NavHostController) {
    NavHost(navController, startDestination = Screen.One.route) {
        composable(Screen.One.route) {
            ScreenOne()
        }
        composable(Screen.Two.route) {
            ScreenTwo(
                onButtonClick = {
                    navigateDestination(navController, Screen.One.route)
                }
            )
        }
    }
}

fun navigateDestination(navController: NavController, destination: String) {
    navController.navigate(destination) {
        launchSingleTop = true
    }
}

sealed class Screen(val route: String, val title: String) {
    object One : Screen("one", "One")
    object Two : Screen("two", "Two")
}