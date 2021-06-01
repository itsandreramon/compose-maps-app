package de.thb.ui.type

import androidx.compose.ui.focus.FocusState

enum class SearchState {
    Inactive, Active, Search
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
        SearchState.Search
    }
}
