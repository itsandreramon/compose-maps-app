package de.thb.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import de.thb.ui.theme.corner_size_medium
import de.thb.ui.theme.margin_medium
import de.thb.ui.type.SearchState
import de.thb.ui.type.getSearchState
import de.thb.ui.util.state

@Composable
fun RulonaSearchBar(
    onSearchStateChanged: (SearchState) -> Unit,
    modifier: Modifier = Modifier,
    onFocusRequested: () -> Unit = {},
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

    Row(modifier, verticalAlignment = Alignment.CenterVertically) {
        AnimatedVisibility(isFocused) {
            Image(
                imageVector = Icons.Default.ArrowBack,
                contentDescription = "Close Search Bar",
                modifier = Modifier
                    .padding(end = margin_medium)
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
            label = { Text("Search") },
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
                .onFocusChanged { focusState ->
                    isFocused = focusState.isFocused
                },
            shape = RoundedCornerShape(corner_size_medium),
            colors = TextFieldDefaults.textFieldColors(
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
            )
        )
    }
}

@Preview
@Composable
fun RulonaSearchBarPreview() {
    RulonaSearchBar(onSearchStateChanged = {})
}
