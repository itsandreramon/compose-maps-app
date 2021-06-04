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
import de.thb.core.data.places.local.PlacesRoomDao
import de.thb.core.domain.PlaceEntity
import de.thb.core.util.fromUtc
import de.thb.core.util.nowUtc
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.components.ScreenTitle
import de.thb.ui.components.places.RulonaPlacesHeader
import de.thb.ui.components.places.RulonaPlacesList
import de.thb.ui.components.search.RulonaSearchHeader
import de.thb.ui.components.search.RulonaSearchList
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
    data class RecentlySearched(
        val recentlySearchedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    data class Search(
        val query: String,
        val currentlySearchedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    data class Bookmarks(
        val bookmarkedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    data class EditBookmarks(
        val bookmarkedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()
}

data class PlacesState(
    val uiState: PlacesUiState = PlacesUiState.Bookmarks(),
) : MavericksState

class PlacesViewModel(
    initialState: PlacesState,
) : MavericksViewModel<PlacesState>(initialState), KoinComponent {

    private val placesLocalDataSource by inject<PlacesLocalDataSource>()

    init {
        populateDb()

        stateFlow.combine(placesLocalDataSource.getAll()) { state, places ->
            when (val uiState = state.uiState) {
                is PlacesUiState.Bookmarks -> {
                    val bookmarkedPlaces = places
                        .filter { it.isBookmarked }
                        .sortedBy { it.name }

                    setState { copy(uiState = PlacesUiState.Bookmarks(bookmarkedPlaces)) }
                }
                is PlacesUiState.RecentlySearched -> {
                    val recentlySearchedPlaces = places
                        .filter { it.searchedAtUtc != null }
                        .sortedByDescending { fromUtc(it.searchedAtUtc!!) }

                    setState { copy(uiState = PlacesUiState.RecentlySearched(recentlySearchedPlaces)) }
                }
                is PlacesUiState.EditBookmarks -> {
                    val bookmarkedPlaces = places
                        .filter { it.isBookmarked }
                        .sortedBy { it.name }

                    setState { copy(uiState = PlacesUiState.EditBookmarks(bookmarkedPlaces)) }
                }
                is PlacesUiState.Search -> {
                    val searchedPlaces = places.filter { place ->
                        place.name.contains(other = uiState.query, ignoreCase = true)
                    }

                    setState { copy(uiState = PlacesUiState.Search(uiState.query, searchedPlaces)) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: PlacesScreenUseCase) {
        when (useCase) {
            is PlacesScreenUseCase.EditBookmarks -> {
                setScreenEditState(useCase.editState)
            }
            is PlacesScreenUseCase.Search -> {
                setScreenSearchState(useCase.searchState)
            }
            is PlacesScreenUseCase.TogglePlaceBookmark -> {
                togglePlaceItemBookmark(useCase.place)
            }
            is PlacesScreenUseCase.SetPlaceSearchTimestamp -> {
                setPlaceSearchedTimestamp(useCase.place)
            }
        }
    }

    private fun setScreenEditState(state: EditState) {
        when (state) {
            is EditState.Editing -> setState { copy(uiState = PlacesUiState.EditBookmarks()) }
            is EditState.Done -> setState { copy(uiState = PlacesUiState.Bookmarks()) }
        }
    }

    private fun setScreenSearchState(state: SearchState) {
        when (state) {
            is SearchState.Active -> setState { copy(uiState = PlacesUiState.RecentlySearched()) }
            is SearchState.Inactive -> setState { copy(uiState = PlacesUiState.Bookmarks()) }
            is SearchState.Search -> setState { copy(uiState = PlacesUiState.Search(state.query)) }
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
                        name = "Berlin"
                    ),
                    PlaceEntity(
                        uuid = "-2",
                        name = "Hamburg",
                        isBookmarked = true,
                        searchedAtUtc = nowUtc()
                    ),
                    PlaceEntity(
                        uuid = "-3",
                        name = "Frankfurt"
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
                viewModel.action(PlacesScreenUseCase.Search(searchState))
            },
            modifier = Modifier.padding(bottom = margin_small),
        )

        when (val uiState = placesUiState.value) {
            is PlacesUiState.Bookmarks -> {
                PlacesBookmarks(
                    bookmarkedPlaces = uiState.bookmarkedPlaces,
                    onEditStateChanged = { viewModel.action(PlacesScreenUseCase.EditBookmarks(it)) },
                    onPlaceClicked = { place -> onPlaceClicked(place.uuid) },
                )
            }
            is PlacesUiState.EditBookmarks -> {
                PlacesEditBookmarks(
                    bookmarkedPlaces = uiState.bookmarkedPlaces,
                    onEditStateChanged = { editState ->
                        viewModel.action(
                            PlacesScreenUseCase.EditBookmarks(
                                editState
                            )
                        )
                    },
                    onItemRemoveClicked = { place ->
                        viewModel.action(
                            PlacesScreenUseCase.TogglePlaceBookmark(
                                place
                            )
                        )
                    },
                )
            }
            is PlacesUiState.RecentlySearched -> {
                PlacesRecentlySearched(
                    recentlySearchedPlaces = uiState.recentlySearchedPlaces,
                    onItemBookmarkClicked = { place ->
                        viewModel.action(
                            PlacesScreenUseCase.TogglePlaceBookmark(
                                place
                            )
                        )
                    },
                    onPlaceClicked = { place -> onPlaceClicked(place.uuid) },
                )
            }
            is PlacesUiState.Search -> {
                PlacesSearch(
                    currentlySearchedPlaces = uiState.currentlySearchedPlaces,
                    onItemBookmarkClicked = { place ->
                        viewModel.action(
                            PlacesScreenUseCase.TogglePlaceBookmark(
                                place
                            )
                        )
                    },
                    onPlaceSearched = { place ->
                        viewModel.action(
                            PlacesScreenUseCase.SetPlaceSearchTimestamp(
                                place
                            )
                        )
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
            onItemBookmarkClicked = onItemBookmarkClicked,
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
    RulonaPlacesHeader(EditState.Done(), onEditStateChanged)

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
    RulonaPlacesHeader(EditState.Editing(), onEditStateChanged)

    if (bookmarkedPlaces.isNotEmpty()) {
        RulonaPlacesList(
            places = bookmarkedPlaces,
            editState = EditState.Editing(),
            onItemRemoved = onItemRemoveClicked,
        )
    }
}
