package de.thb.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.Close
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
import androidx.compose.ui.unit.dp
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.margin_small
import de.thb.ui.type.SearchState
import de.thb.ui.type.getSearchState
import de.thb.ui.util.state

@Composable
fun RulonaSearchBarFilled(
    onSearchStateChanged: (SearchState) -> Unit,
    onFocusRequested: () -> Unit,
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

    Box(Modifier.fillMaxWidth()) {
        Card(
            shape = RoundedCornerShape(32.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = margin_medium),
            elevation = 4.dp,
        ) {
            Row(
                modifier = Modifier
                    .padding(horizontal = margin_small),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AnimatedVisibility(isFocused) {
                    Image(
                        imageVector = Icons.Default.ChevronLeft,
                        colorFilter = ColorFilter.tint(MaterialTheme.colors.onBackground),
                        contentDescription = "Close Search Bar",
                        modifier = Modifier
                            .padding(horizontal = margin_small)
                            .clickable {
                                isFocused = false
                                onFocusRequested()
                            }
                    )
                }

                TextField(
                    value = query,
                    onValueChange = { input ->
                        onSearchStateChanged(getSearchState(input.text, isFocused))
                        query = input
                    },
                    placeholder = { Text("Search") },
                    colors = TextFieldDefaults.textFieldColors(
                        backgroundColor = Color.Transparent,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                    ),
                    trailingIcon = {
                        if (isFocused) {
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
                        .padding(top = 2.dp)
                        .onFocusChanged { focusState ->
                            isFocused = focusState.isFocused
                        },
                )
            }
        }
    }
}
