package de.thb.ui.screens.places

import android.util.Log
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.util.fromUtc
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
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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

    object BookmarksUiState : PlacesUiState()

    object EditBookmarksUiState : PlacesUiState()
}

data class PlacesState(
    val uiState: PlacesUiState = BookmarksUiState,
    val bookmarkedPlaces: List<PlaceEntity> = listOf(),
) : MavericksState

class PlacesViewModel(
    initialState: PlacesState,
) : MavericksViewModel<PlacesState>(initialState), KoinComponent {

    private val placesRepository by inject<PlacesRepository>()

    init {
        placesRepository.getAll()
            .onEach { places ->
                val bookmarkedPlaces = places
                    .filter { it.isBookmarked }
                    .sortedBy { it.name }

                setState { copy(bookmarkedPlaces = bookmarkedPlaces) }
            }
            .launchIn(viewModelScope)

        stateFlow.combine(placesRepository.getAll()) { state, places ->
            when (val uiState = state.uiState) {
                is RecentlySearchedUiState -> {
                    val recentlySearchedPlaces = places
                        .filter { it.searchedAtUtc != null }
                        .sortedByDescending { fromUtc(it.searchedAtUtc!!) }

                    setState { copy(uiState = uiState.copy(recentlySearchedPlaces)) }
                }
                is SearchUiState -> {
                    val searchedPlaces = places.filter {
                        it.name.contains(uiState.query, ignoreCase = true)
                    }

                    setState { copy(uiState = uiState.copy(uiState.query, searchedPlaces)) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: PlacesScreenUseCase) {
        when (useCase) {
            is EditBookmarksUseCase -> setScreenEditState(useCase.editState)
            is SearchUseCase -> setScreenSearchState(useCase.searchState)
            is TogglePlaceBookmarkUseCase -> togglePlaceItemBookmark(useCase.place)
            is SetPlaceSearchTimestampUseCase -> setPlaceSearchedTimestamp(useCase.place)
        }
    }

    private fun setScreenEditState(state: EditState) {
        when (state) {
            is EditState.Editing -> setState { copy(uiState = EditBookmarksUiState) }
            else -> setState { copy(uiState = BookmarksUiState) }
        }
    }

    private fun setScreenSearchState(state: SearchState) {
        when (state) {
            is SearchState.Active -> setState { copy(uiState = RecentlySearchedUiState()) }
            is SearchState.Inactive -> setState { copy(uiState = BookmarksUiState) }
            is SearchState.Search -> setState { copy(uiState = SearchUiState(state.query)) }
        }
    }

    private fun togglePlaceItemBookmark(place: PlaceEntity) {
        viewModelScope.launch {
            val updatedPlace = place.copy(isBookmarked = !place.isBookmarked)
            placesRepository.insert(updatedPlace)
        }
    }

    private fun setPlaceSearchedTimestamp(place: PlaceEntity) {
        viewModelScope.launch {
            val updatedPlace = place.copy(searchedAtUtc = Instant.now().toString())
            placesRepository.insert(updatedPlace)
        }
    }
}

@Composable
fun PlacesScreen(
    viewModel: PlacesViewModel = mavericksViewModel(),
    onPlaceClicked: (id: String) -> Unit
) {
    Log.e("Recomposition", "PlacesScreen")

    val placesUiState = viewModel.collectAsState(PlacesState::uiState)
    val bookmarkedPlaces by viewModel.collectAsState(PlacesState::bookmarkedPlaces)

    val focusRequester = FocusRequester()

    Column(
        Modifier
            .statusBarsPadding()
            .padding(margin_medium)
            .focusRequester(focusRequester)
            .focusTarget()
    ) {
        ScreenTitle(title = "Orte", Modifier.padding(vertical = margin_medium))

        RulonaSearchBar(
            onSearchStateChanged = { searchState ->
                viewModel.action(SearchUseCase(searchState))
            },
            onFocusRequested = { focusRequester.requestFocus() }
        )

        when (val uiState = placesUiState.value) {
            is BookmarksUiState -> {
                PlacesBookmarks(
                    bookmarkedPlaces = bookmarkedPlaces,
                    onEditStateChanged = { viewModel.action(EditBookmarksUseCase(it)) },
                    onPlaceClicked = { place -> onPlaceClicked(place.id) },
                )
            }
            is EditBookmarksUiState -> {
                PlacesEditBookmarks(
                    bookmarkedPlaces = bookmarkedPlaces,
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
                    onPlaceClicked = { place -> onPlaceClicked(place.id) },
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
                    onPlaceClicked = { place -> onPlaceClicked(place.id) },
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
            onItemClick = { id ->
                onPlaceClicked(id)
                onPlaceSearched(id)
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
    RulonaHeaderEditable(
        title = "Meine Orte",
        editState = EditState.Done(),
        onEditStateChanged = onEditStateChanged
    )

    val alpha by animateFloatAsState(
        targetValue = if (bookmarkedPlaces.isNotEmpty()) 1f else 0f,
        animationSpec = tween(500)
    )

    Box(Modifier.alpha(alpha)) {
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
    RulonaHeaderEditable(
        title = "Meine Orte",
        editState = EditState.Editing(),
        onEditStateChanged = onEditStateChanged
    )

    if (bookmarkedPlaces.isNotEmpty()) {
        RulonaPlacesList(
            places = bookmarkedPlaces,
            editState = EditState.Editing(),
            onItemRemoved = onItemRemoveClicked,
        )
    }
}
