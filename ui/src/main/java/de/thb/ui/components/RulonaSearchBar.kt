package de.thb.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_material_orange_600
import de.thb.ui.type.SearchState
import de.thb.ui.type.getSearchState
import de.thb.ui.util.state

@Composable
fun RulonaSearchBar(
    onSearchStateChanged: (SearchState) -> Unit,
    modifier: Modifier = Modifier,
    onFocusRequested: () -> Unit = {},
    onCurrentLocationClicked: () -> Unit = {},
) {
    var query by state { TextFieldValue() }
    var isFocused by state { false }

    // handle search state changes outside of onValueChanged
    LaunchedEffect(isFocused) {
        if (!isFocused) {
            // if no longer in focus, reset
            query = TextFieldValue()
            onSearchStateChanged(SearchState.Inactive())
        } else {
            if (query.text.isBlank()) {
                onSearchStateChanged(SearchState.Active())
            }
        }
    }

    Column {
        // search field
        Row(modifier, verticalAlignment = Alignment.CenterVertically) {
            AnimatedVisibility(isFocused) {
                Image(
                    imageVector = Icons.Default.ChevronLeft,
                    colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                    contentDescription = "Close Search Bar",
                    modifier = Modifier
                        .padding(end = margin_medium)
                        .clickable {
                            isFocused = false
                            onFocusRequested()
                        }
                )
            }

            OutlinedTextField(
                value = query,
                maxLines = 1,
                onValueChange = { input ->
                    onSearchStateChanged(getSearchState(input.text, isFocused))
                    query = input
                },
                label = {
                    Text(
                        text = "Suchen",
                        color = if (isFocused) {
                            rulona_material_orange_600
                        } else {
                            MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
                        }
                    )
                },
                trailingIcon = {
                    if (isFocused && query.text.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Clear Search Bar Icon",
                            modifier = Modifier
                                .clickable {
                                    query = TextFieldValue()
                                    onSearchStateChanged(SearchState.Active())
                                }
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Filled.Search,
                            contentDescription = "Search Icon",
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged { focusState ->
                        isFocused = focusState.isFocused
                    },
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = rulona_material_orange_600,
                    backgroundColor = Color.Green,
                    cursorColor = rulona_material_orange_600,
                )
            )
        }

        Spacer(Modifier.height(margin_medium))

        // current location
        Surface(shape = RoundedCornerShape(margin_medium)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onCurrentLocationClicked() }
            ) {
                Image(
                    imageVector = Icons.Default.MyLocation,
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(rulona_material_orange_600),
                    modifier = Modifier.padding(margin_medium)
                )

                Text(text = "Aktueller Standort")
            }
        }
    }
}

@Preview
@Composable
fun RulonaSearchBarPreview() {
    RulonaSearchBar(onSearchStateChanged = {})
}
