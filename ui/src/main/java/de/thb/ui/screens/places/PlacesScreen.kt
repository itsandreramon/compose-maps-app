package de.thb.ui.screens.places

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant
import java.util.Locale

data class PlacesState(
    val editState: EditState = EditState.Done,
    val searchState: SearchState = SearchState.Inactive,
    val places: List<PlaceEntity> = listOf(),
    val currentlySearchedPlaces: List<PlaceEntity> = listOf(),
    val recentlySearchedPlaces: List<PlaceEntity> = listOf(),
    val bookmarkedPlaces: List<PlaceEntity> = listOf(),
) : MavericksState

class PlacesViewModel(
    initialState: PlacesState,
) : MavericksViewModel<PlacesState>(initialState), KoinComponent {

    private val placesLocalDataSource by inject<PlacesLocalDataSource>()

    init {
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

        viewModelScope.launch {
            placesLocalDataSource
                .getAll()
                .collect {
                    setState { copy(places = it) }
                }
        }

        stateFlow.onEach { state ->
            val bookmarkedPlaces = state.places
                .filter { it.isBookmarked }
                .sortedBy { it.name }

            setState { copy(bookmarkedPlaces = bookmarkedPlaces) }
        }.launchIn(viewModelScope)

        stateFlow.onEach { state ->
            val recentlySearchedPlaces = state.places
                .filter { it.searchedAtUtc != null }
                .sortedByDescending { fromUtc(it.searchedAtUtc!!) }

            setState { copy(recentlySearchedPlaces = recentlySearchedPlaces) }
        }.launchIn(viewModelScope)

        stateFlow.onEach { state ->
            val searchedPlaces = if (state.searchState is SearchState.Search) {
                state.places.filter { place ->
                    place.name
                        .toLowerCase(Locale.getDefault())
                        .contains(state.searchState.query)
                }
            } else {
                listOf()
            }

            setState { copy(currentlySearchedPlaces = searchedPlaces) }
        }.launchIn(viewModelScope)
    }

    fun setScreenEditState(state: EditState) {
        setState { copy(editState = state) }
    }

    fun setScreenSearchState(state: SearchState) {
        setState { copy(searchState = state) }
    }

    fun togglePlaceItemBookmark(place: PlaceEntity) {
        viewModelScope.launch {
            val updatedPlace = place.copy(
                isBookmarked = !place.isBookmarked
            )

            placesLocalDataSource.insert(updatedPlace)
        }
    }

    fun setPlaceSearchedTimestamp(uuid: String) {
        viewModelScope.launch {
            val searchedPlace = placesLocalDataSource
                .getByUuid(uuid)
                .firstOrNull()

            if (searchedPlace != null) {
                val updatedPlace = searchedPlace.copy(
                    searchedAtUtc = Instant.now().toString()
                )

                placesLocalDataSource.insert(updatedPlace)
            }
        }
    }
}

@Composable
fun PlacesScreen(
    viewModel: PlacesViewModel = mavericksViewModel(),
    onPlaceClicked: (uuid: String) -> Unit
) {
    val bookmarkedPlaces by viewModel.collectAsState(PlacesState::bookmarkedPlaces)
    val currentlySearchedPlaces by viewModel.collectAsState(PlacesState::currentlySearchedPlaces)
    val recentlySearchedPlaces by viewModel.collectAsState(PlacesState::recentlySearchedPlaces)

    val editState by viewModel.collectAsState(PlacesState::editState)
    val searchState by viewModel.collectAsState(PlacesState::searchState)

    PlacesScreenContent(
        bookmarkedPlaces = bookmarkedPlaces,
        currentlySearchedPlaces = currentlySearchedPlaces,
        recentlySearchedPlaces = recentlySearchedPlaces,
        editState = editState,
        searchState = searchState,
        onPlaceClicked = onPlaceClicked,
        onPlaceSearched = viewModel::setPlaceSearchedTimestamp,
        onSearchStateChanged = viewModel::setScreenSearchState,
        onEditStateChanged = viewModel::setScreenEditState,
        onItemBookmarkClicked = viewModel::togglePlaceItemBookmark,
        onItemRemoveClicked = viewModel::togglePlaceItemBookmark
    )
}

@Composable
fun PlacesScreenContent(
    bookmarkedPlaces: List<PlaceEntity>,
    currentlySearchedPlaces: List<PlaceEntity>,
    recentlySearchedPlaces: List<PlaceEntity>,
    editState: EditState = EditState.Done,
    searchState: SearchState = SearchState.Inactive,
    onPlaceClicked: (uuid: String) -> Unit,
    onPlaceSearched: (uuid: String) -> Unit,
    onSearchStateChanged: (SearchState) -> Unit,
    onEditStateChanged: (EditState) -> Unit,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
    onItemRemoveClicked: (PlaceEntity) -> Unit,
) {
    Column(
        Modifier
            .statusBarsPadding()
            .padding(margin_medium)
    ) {
        ScreenTitle(title = "Places", Modifier.padding(vertical = margin_medium))

        RulonaSearchBar(
            onSearchStateChanged = onSearchStateChanged,
            modifier = Modifier.padding(bottom = margin_small),
        )

        when (searchState) {
            is SearchState.Inactive -> {
                RulonaPlacesHeader(editState, onEditStateChanged)

                RulonaPlacesList(
                    places = bookmarkedPlaces,
                    editState = editState,
                    onItemClick = onPlaceClicked,
                    onItemRemoved = onItemRemoveClicked,
                )
            }
            is SearchState.Active -> {
                RulonaSearchHeader()

                RulonaSearchList(
                    places = recentlySearchedPlaces,
                    onItemClick = onPlaceClicked,
                    onItemBookmarkClicked = onItemBookmarkClicked
                )
            }
            is SearchState.Search -> {
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
    }
}
