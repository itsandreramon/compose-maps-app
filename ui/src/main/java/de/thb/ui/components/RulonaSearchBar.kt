package de.thb.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.tooling.preview.Preview
import de.thb.ui.theme.corner_size_medium
import de.thb.ui.type.SearchState
import de.thb.ui.type.getSearchState
import de.thb.ui.util.state

@Composable
fun RulonaSearchBar(
    onSearchStateChanged: (SearchState) -> Unit,
    modifier: Modifier = Modifier
) {
    var query by state { TextFieldValue() }
    var isFocused by state { false }

    // if no longer in focus, reset search query
    if (!isFocused) {
        query = TextFieldValue()
    }

    val searchState = getSearchState(query.text, isFocused)

    onSearchStateChanged(searchState)

    Box(modifier) {
        TextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search") },
            trailingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { focusState -> isFocused = focusState.hasFocus },
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
