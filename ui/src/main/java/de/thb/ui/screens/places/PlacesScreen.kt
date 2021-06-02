package de.thb.ui.screens.places

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.domain.PlaceEntity
import de.thb.ui.components.RulonaPlacesHeader
import de.thb.ui.components.RulonaPlacesList
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.components.RulonaSearchHeader
import de.thb.ui.components.RulonaSearchList
import de.thb.ui.components.ScreenTitle
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.util.Locale

data class PlacesState(
    val editState: EditState = EditState.Done,
    val searchState: SearchState = SearchState.Inactive,
    val places: List<PlaceEntity> = listOf(),
    val searchedPlaces: List<PlaceEntity> = listOf(),
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
                    PlaceEntity(uuid = "-1", name = "Berlin"),
                    PlaceEntity(uuid = "-2", name = "Hamburg"),
                    PlaceEntity(uuid = "-3", name = "Frankfurt"),
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

            setState { copy(bookmarkedPlaces = bookmarkedPlaces) }
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

            setState { copy(searchedPlaces = searchedPlaces) }
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
}

@Composable
fun PlacesScreen(viewModel: PlacesViewModel = mavericksViewModel()) {
    val bookmarkedPlaces by viewModel.collectAsState(PlacesState::bookmarkedPlaces)
    val searchedPlaces by viewModel.collectAsState(PlacesState::searchedPlaces)

    val editState by viewModel.collectAsState(PlacesState::editState)
    val searchState by viewModel.collectAsState(PlacesState::searchState)

    PlacesScreenContent(
        bookmarkedPlaces = bookmarkedPlaces,
        searchedPlaces = searchedPlaces,
        editState = editState,
        searchState = searchState,
        onSearchStateChanged = viewModel::setScreenSearchState,
        onEditStateChanged = viewModel::setScreenEditState,
        onItemBookmarkClicked = viewModel::togglePlaceItemBookmark,
    )
}

@Composable
fun PlacesScreenContent(
    bookmarkedPlaces: List<PlaceEntity>,
    searchedPlaces: List<PlaceEntity>,
    editState: EditState = EditState.Done,
    searchState: SearchState = SearchState.Inactive,
    onSearchStateChanged: (SearchState) -> Unit,
    onEditStateChanged: (EditState) -> Unit,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
) {
    Column(
        Modifier
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        ScreenTitle(title = "Places", Modifier.padding(vertical = 16.dp))

        RulonaSearchBar(
            onSearchStateChanged = onSearchStateChanged,
            modifier = Modifier.padding(bottom = 8.dp),
        )

        when (searchState) {
            is SearchState.Inactive -> {
                RulonaPlacesHeader(editState, onEditStateChanged)

                RulonaPlacesList(
                    places = bookmarkedPlaces,
                    onItemClick = { Log.e("TAG", "Clicked") },
                    editState = editState,
                )
            }
            is SearchState.Active -> {
                RulonaSearchHeader()

                RulonaSearchList(
                    places = bookmarkedPlaces,
                    onItemClick = {},
                    onItemBookmarkClicked = onItemBookmarkClicked
                )
            }
            is SearchState.Search -> {
                RulonaSearchList(
                    places = searchedPlaces,
                    onItemClick = {},
                    onItemBookmarkClicked = onItemBookmarkClicked,
                )
            }
        }
    }
}
