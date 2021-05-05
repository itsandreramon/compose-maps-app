package de.thb.rulona.ui

import androidx.compose.foundation.background
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Scaffold
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.KEY_ROUTE
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.navigate
import androidx.navigation.compose.rememberNavController
import com.google.accompanist.insets.navigationBarsPadding
import de.thb.rulona.nav.NavContainer
import de.thb.rulona.nav.Screen

@Composable
fun AppContainer() {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = { RulonaBottomAppBar(navController) },
        content = { AppContent(navController) }
    )
}

@Composable
fun AppContent(navController: NavHostController) {
    NavContainer(navController)
}

@Composable
fun RulonaBottomAppBar(navController: NavHostController) {
    val items = listOf(Screen.One, Screen.Two)

    Surface(
        elevation = 4.dp,
        modifier = Modifier.background(MaterialTheme.colors.surface)
    ) {
        BottomNavigation(
            elevation = 0.dp,
            backgroundColor = Color.Transparent,
            modifier = Modifier.navigationBarsPadding(),
        ) {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentRoute = navBackStackEntry?.arguments?.getString(KEY_ROUTE)

            items.forEach { screen ->
                BottomNavigationItem(
                    icon = { Icon(Icons.Filled.Favorite, contentDescription = null) },
                    label = { Text(screen.title) },
                    selected = currentRoute == screen.route,
                    onClick = {
                        navController.navigate(screen.route) {
                            popUpTo = navController.graph.startDestination
                            launchSingleTop = true
                        }
                    }
                )
            }
        }
    }
}
