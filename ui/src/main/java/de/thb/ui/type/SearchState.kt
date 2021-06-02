package de.thb.ui.type

import androidx.compose.ui.focus.FocusState
import java.util.Locale

sealed class SearchState(val query: String = "") {
    object Inactive : SearchState()
    object Active : SearchState()
    class Search(query: String) : SearchState(query)
}

fun getSearchState(
    searchQuery: String,
    focusState: FocusState
): SearchState {
    return if (searchQuery.isEmpty()) {
        if (focusState == FocusState.Active) {
            SearchState.Active
        } else {
            SearchState.Inactive
        }
    } else {
        SearchState.Search(searchQuery.toLowerCase(Locale.getDefault()))
    }
}
