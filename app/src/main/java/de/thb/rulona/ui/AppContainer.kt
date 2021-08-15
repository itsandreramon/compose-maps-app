package de.thb.rulona.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsPadding
import de.thb.rulona.nav.NavContainer
import de.thb.ui.components.RulonaBottomNavigation
import de.thb.ui.screens.Screen

@Composable
fun AppContainer() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { RulonaBottomAppBar(navController) },
        content = {
            Box(Modifier.navigationBarsPadding()) {
                Box(Modifier.padding(bottom = 56.dp)) {
                    AppContent(navController)
                }
            }
        }
    )
}

@Composable
fun AppContent(navController: NavHostController) {
    NavContainer(navController)
}

@Composable
fun RulonaBottomAppBar(navController: NavHostController) {
    val items = listOf(Screen.Places, Screen.Route)
    val currentNavBackStackEntry by navController.currentBackStackEntryAsState()

    RulonaBottomNavigation(
        items = items,
        currentNavBackStackEntry = currentNavBackStackEntry,
        onClick = { screen ->
            navController.navigate(screen.route) {
                popUpTo(navController.graph.startDestinationRoute!!) {
                    saveState = true
                }

                launchSingleTop = true
            }
        }
    )
}
