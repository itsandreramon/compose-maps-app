package de.thb.rulona

import androidx.activity.ComponentActivity
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.navigation.compose.rememberNavController
import com.airbnb.mvrx.Mavericks
import de.thb.rulona.nav.NavContainer
import de.thb.ui.theme.RulonaTheme
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
            RulonaTheme {
                NavContainer(navController = rememberNavController())
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

        composeTestRule.onNodeWithText("Hamburg")
            .assertDoesNotExist()
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

    @Test
    fun edit_button_changes_edit_state() {
        composeTestRule.onNodeWithContentDescription("Open Place Details")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Edit Places")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Remove Bookmark")
            .assertExists()
    }

    @Test
    fun clicking_on_bookmark_opens_details_screen() {
        composeTestRule.onNodeWithText("Hamburg")
            .performClick()

        composeTestRule.onNodeWithText("Meine Filter")
            .assertIsDisplayed()
    }

    @Test
    fun clicking_on_close_inside_search_bar_resets_state() {
        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithContentDescription("Clear Search Bar Icon")
            .performClick()

        composeTestRule.onNodeWithText("Letzte Suchen")
            .assertIsDisplayed()
    }

    @Test
    fun clicking_on_search_bar_changes_icon() {
        composeTestRule.onNodeWithContentDescription("Search Icon")
            .assertIsDisplayed()

        composeTestRule.onNodeWithText("Search")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Clear Search Bar Icon")
            .assertIsDisplayed()

        composeTestRule.onNodeWithContentDescription("Search Icon")
            .assertDoesNotExist()
    }

    @Test
    fun clicking_on_back_inside_search_bar_resets_state() {
        composeTestRule.onNodeWithContentDescription("Close Search Bar")
            .assertDoesNotExist()

        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithContentDescription("Close Search Bar")
            .performClick()

        composeTestRule.onNodeWithContentDescription("Close Search Bar")
            .assertDoesNotExist()

        composeTestRule.onNodeWithText("Meine Orte")
            .assertIsDisplayed()
    }

    @Test
    fun search_bar_clicking_on_back_removes_query() {
        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithContentDescription("Close Search Bar")
            .performClick()

        composeTestRule.onNodeWithText("Ber")
            .assertDoesNotExist()
    }

    @Test
    fun search_bar_clicking_on_close_removes_query() {
        composeTestRule.onNodeWithText("Search")
            .performTextInput("Ber")

        composeTestRule.onNodeWithContentDescription("Clear Search Bar Icon")
            .performClick()

        composeTestRule.onNodeWithText("Ber")
            .assertDoesNotExist()
    }
}
