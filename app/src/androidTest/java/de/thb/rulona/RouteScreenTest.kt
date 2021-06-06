package de.thb.rulona

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.Mavericks
import de.thb.rulona.nav.NavContainer
import de.thb.rulona.nav.Screen
import de.thb.ui.theme.RulonaTheme
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RouteScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    private lateinit var navController: NavHostController

    @Before
    fun setUp() {
        Mavericks.initialize(composeTestRule.activity)

        composeTestRule.setContent {
            RulonaTheme {
                navController = rememberNavController()
                NavContainer(navController = navController)
            }
        }

        composeTestRule.runOnUiThread {
            navController.navigate(Screen.Route.route)
        }
    }

    @Test
    fun route_overview_screen_displays_google_map() {
        composeTestRule.onNodeWithText("Google Map")
            .assertIsDisplayed()
    }

    @Test
    fun search_bar_changes_state_correctly() {
        composeTestRule.onNodeWithText("Search")
            .performClick()

        composeTestRule.onNodeWithText("Google Map")
            .assertDoesNotExist()

        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithText("Berlin")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Close Search Bar")
            .performClick()

        composeTestRule.onNodeWithText("Google Map")
            .assertIsDisplayed()
    }

    @Test
    fun search_bar_clicking_on_item_opens_details() {
        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithText("Berlin")
            .performClick()

        composeTestRule.onNodeWithText("Berlin")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Back", useUnmergedTree = true)
            .performClick()

        composeTestRule.onNodeWithText("Google Map")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Berlin")
            .assertDoesNotExist()
    }
}
