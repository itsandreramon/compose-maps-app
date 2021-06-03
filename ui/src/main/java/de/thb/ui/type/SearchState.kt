package de.thb.ui.type

import java.util.Locale

sealed class SearchState(val query: String = "") {
    object Inactive : SearchState()
    object Active : SearchState()
    class Search(query: String) : SearchState(query)
}

fun getSearchState(
    searchQuery: String,
    isFocused: Boolean
): SearchState {
    return if (searchQuery.isEmpty()) {
        if (isFocused) {
            SearchState.Active
        } else {
            SearchState.Inactive
        }
    } else {
        SearchState.Search(searchQuery.lowercase(Locale.getDefault()))
    }
}
