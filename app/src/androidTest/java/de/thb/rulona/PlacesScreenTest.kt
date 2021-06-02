package de.thb.rulona

import androidx.activity.ComponentActivity
import androidx.compose.material.MaterialTheme
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import com.airbnb.mvrx.Mavericks
import de.thb.ui.screens.places.PlacesScreen
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class PlacesScreenTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Before
    fun setUp() {
        Mavericks.initialize(composeTestRule.activity)

        composeTestRule.setContent {
            MaterialTheme {
                PlacesScreen()
            }
        }
    }

    @Test
    fun search_bar_focus_changes_search_state() {
        composeTestRule.onNodeWithText("Meine Orte")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Search")
            .performClick()

        composeTestRule.onNodeWithText("Letzte Suchen")
            .assertIsDisplayed()
    }

    @Test
    fun search_bar_input_changes_search_state() {
        composeTestRule.onNodeWithText("Meine Orte")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithText("Berlin")
            .assertIsDisplayed()
    }

    @Test
    fun search_bar_not_focused_resets_search_state() {
        composeTestRule.onNodeWithText("Meine Orte")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithText("Places")
            .performClick()

        composeTestRule.onNodeWithText("Meine Orte")
            .assertIsDisplayed()
    }
}
