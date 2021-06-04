package de.thb.ui.screens.places

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.domain.PlaceEntity
import de.thb.core.util.fromUtc
import de.thb.core.util.nowUtc
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.components.ScreenTitle
import de.thb.ui.components.places.RulonaPlacesList
import de.thb.ui.components.search.RulonaSearchHeader
import de.thb.ui.components.search.RulonaSearchList
import de.thb.ui.screens.places.PlacesScreenUseCase.EditBookmarksUseCase
import de.thb.ui.screens.places.PlacesScreenUseCase.SearchUseCase
import de.thb.ui.screens.places.PlacesScreenUseCase.SetPlaceSearchTimestampUseCase
import de.thb.ui.screens.places.PlacesScreenUseCase.TogglePlaceBookmarkUseCase
import de.thb.ui.screens.places.PlacesUiState.BookmarksUiState
import de.thb.ui.screens.places.PlacesUiState.EditBookmarksUiState
import de.thb.ui.screens.places.PlacesUiState.RecentlySearchedUiState
import de.thb.ui.screens.places.PlacesUiState.SearchUiState
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.margin_small
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant

sealed class PlacesUiState {
    data class RecentlySearchedUiState(
        val recentlySearchedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    data class SearchUiState(
        val query: String,
        val currentlySearchedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    data class BookmarksUiState(
        val bookmarkedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    data class EditBookmarksUiState(
        val bookmarkedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()
}

data class PlacesState(
    val uiState: PlacesUiState = BookmarksUiState(),
) : MavericksState

class PlacesViewModel(
    initialState: PlacesState,
) : MavericksViewModel<PlacesState>(initialState), KoinComponent {

    private val placesLocalDataSource by inject<PlacesLocalDataSource>()

    init {
        populateDb()

        stateFlow.combine(placesLocalDataSource.getAll()) { state, places ->
            when (val uiState = state.uiState) {
                is BookmarksUiState -> {
                    val bookmarkedPlaces = places
                        .filter { it.isBookmarked }
                        .sortedBy { it.name }

                    setState { copy(uiState = BookmarksUiState(bookmarkedPlaces)) }
                }
                is RecentlySearchedUiState -> {
                    val recentlySearchedPlaces = places
                        .filter { it.searchedAtUtc != null }
                        .sortedByDescending { fromUtc(it.searchedAtUtc!!) }

                    setState { copy(uiState = RecentlySearchedUiState(recentlySearchedPlaces)) }
                }
                is EditBookmarksUiState -> {
                    val bookmarkedPlaces = places
                        .filter { it.isBookmarked }
                        .sortedBy { it.name }

                    setState { copy(uiState = EditBookmarksUiState(bookmarkedPlaces)) }
                }
                is SearchUiState -> {
                    val searchedPlaces = places.filter {
                        it.name.contains(uiState.query, ignoreCase = true)
                    }

                    setState { copy(uiState = SearchUiState(uiState.query, searchedPlaces)) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: PlacesScreenUseCase) {
        when (useCase) {
            is EditBookmarksUseCase -> {
                setScreenEditState(useCase.editState)
            }
            is SearchUseCase -> {
                setScreenSearchState(useCase.searchState)
            }
            is TogglePlaceBookmarkUseCase -> {
                togglePlaceItemBookmark(useCase.place)
            }
            is SetPlaceSearchTimestampUseCase -> {
                setPlaceSearchedTimestamp(useCase.place)
            }
        }
    }

    private fun setScreenEditState(state: EditState) {
        when (state) {
            is EditState.Editing -> setState { copy(uiState = EditBookmarksUiState()) }
            is EditState.Done -> setState { copy(uiState = BookmarksUiState()) }
        }
    }

    private fun setScreenSearchState(state: SearchState) {
        when (state) {
            is SearchState.Active -> setState { copy(uiState = RecentlySearchedUiState()) }
            is SearchState.Inactive -> setState { copy(uiState = BookmarksUiState()) }
            is SearchState.Search -> setState { copy(uiState = SearchUiState(state.query)) }
        }
    }

    private fun togglePlaceItemBookmark(place: PlaceEntity) {
        viewModelScope.launch {
            val updatedPlace = place.copy(isBookmarked = !place.isBookmarked)
            placesLocalDataSource.insert(updatedPlace)
        }
    }

    private fun setPlaceSearchedTimestamp(place: PlaceEntity) {
        viewModelScope.launch {
            val updatedPlace = place.copy(searchedAtUtc = Instant.now().toString())
            placesLocalDataSource.insert(updatedPlace)
        }
    }

    /**
     * Temporary initialization until we have real data.
     */
    private fun populateDb() {
        viewModelScope.launch {
            placesLocalDataSource.insert(
                listOf(
                    PlaceEntity(
                        uuid = "-1",
                        name = "Hamburg",
                        isBookmarked = true,
                        searchedAtUtc = nowUtc()
                    ),
                    PlaceEntity(
                        uuid = "-2",
                        name = "Frankfurt"
                    ),
                    PlaceEntity(
                        uuid = "-3",
                        name = "Berlin"
                    ),
                )
            )
        }
    }
}

@Composable
fun PlacesScreen(
    viewModel: PlacesViewModel = mavericksViewModel(),
    onPlaceClicked: (uuid: String) -> Unit
) {
    val placesUiState = viewModel.collectAsState(PlacesState::uiState)

    Column(
        Modifier
            .statusBarsPadding()
            .padding(margin_medium)
    ) {
        ScreenTitle(title = "Places", Modifier.padding(vertical = margin_medium))

        RulonaSearchBar(
            onSearchStateChanged = { searchState ->
                viewModel.action(SearchUseCase(searchState))
            },
            modifier = Modifier.padding(bottom = margin_small),
        )

        when (val uiState = placesUiState.value) {
            is BookmarksUiState -> {
                PlacesBookmarks(
                    bookmarkedPlaces = uiState.bookmarkedPlaces,
                    onEditStateChanged = { viewModel.action(EditBookmarksUseCase(it)) },
                    onPlaceClicked = { place -> onPlaceClicked(place.uuid) },
                )
            }
            is EditBookmarksUiState -> {
                PlacesEditBookmarks(
                    bookmarkedPlaces = uiState.bookmarkedPlaces,
                    onEditStateChanged = { editState ->
                        viewModel.action(EditBookmarksUseCase(editState))
                    },
                    onItemRemoveClicked = { place ->
                        viewModel.action(TogglePlaceBookmarkUseCase(place))
                    },
                )
            }
            is RecentlySearchedUiState -> {
                PlacesRecentlySearched(
                    recentlySearchedPlaces = uiState.recentlySearchedPlaces,
                    onItemBookmarkClicked = { place ->
                        viewModel.action(TogglePlaceBookmarkUseCase(place))
                    },
                    onPlaceClicked = { place -> onPlaceClicked(place.uuid) },
                )
            }
            is SearchUiState -> {
                PlacesSearch(
                    currentlySearchedPlaces = uiState.currentlySearchedPlaces,
                    onItemBookmarkClicked = { place ->
                        viewModel.action(TogglePlaceBookmarkUseCase(place))
                    },
                    onPlaceSearched = { place ->
                        viewModel.action(SetPlaceSearchTimestampUseCase(place))
                    },
                    onPlaceClicked = { place -> onPlaceClicked(place.uuid) },
                )
            }
        }
    }
}

@Composable
fun PlacesSearch(
    currentlySearchedPlaces: List<PlaceEntity>,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
    onPlaceSearched: (PlaceEntity) -> Unit,
    onPlaceClicked: (PlaceEntity) -> Unit,
) {
    if (currentlySearchedPlaces.isNotEmpty()) {
        RulonaSearchList(
            places = currentlySearchedPlaces,
            onItemClick = { uuid ->
                onPlaceClicked(uuid)
                onPlaceSearched(uuid)
            },
            onItemBookmarkClicked = {
                onItemBookmarkClicked(it)
            },
        )
    }
}

@Composable
fun PlacesRecentlySearched(
    recentlySearchedPlaces: List<PlaceEntity>,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
    onPlaceClicked: (PlaceEntity) -> Unit,
) {
    RulonaSearchHeader()

    if (recentlySearchedPlaces.isNotEmpty()) {
        RulonaSearchList(
            places = recentlySearchedPlaces,
            onItemClick = onPlaceClicked,
            onItemBookmarkClicked = onItemBookmarkClicked
        )
    }
}

@Composable
fun PlacesBookmarks(
    bookmarkedPlaces: List<PlaceEntity>,
    onEditStateChanged: (EditState) -> Unit,
    onPlaceClicked: (PlaceEntity) -> Unit,
) {
    RulonaHeaderEditable("Meine Orte", EditState.Done(), onEditStateChanged)

    if (bookmarkedPlaces.isNotEmpty()) {
        RulonaPlacesList(
            places = bookmarkedPlaces,
            editState = EditState.Done(),
            onItemClick = onPlaceClicked,
        )
    }
}

@Composable
fun PlacesEditBookmarks(
    bookmarkedPlaces: List<PlaceEntity>,
    onEditStateChanged: (EditState) -> Unit,
    onItemRemoveClicked: (PlaceEntity) -> Unit,
) {
    RulonaHeaderEditable("Meine Orte", EditState.Editing(), onEditStateChanged)

    if (bookmarkedPlaces.isNotEmpty()) {
        RulonaPlacesList(
            places = bookmarkedPlaces,
            editState = EditState.Editing(),
            onItemRemoved = onItemRemoveClicked,
        )
    }
}
