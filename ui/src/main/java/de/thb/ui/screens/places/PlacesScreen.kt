package de.thb.ui.screens.places

import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
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
import de.thb.ui.components.*
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class PlacesState(
    val editState: EditState = EditState.Done,
    val searchState: SearchState = SearchState.Inactive,
    val places: List<PlaceEntity> = listOf(),
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
                .collect { setState { copy(places = it) } }
        }
    }

    fun setEditState(state: EditState) {
        setState { copy(editState = state) }
    }

    fun setSearchState(state: SearchState) {
        setState { copy(searchState = state) }
    }
}

@Composable
fun PlacesScreen(viewModel: PlacesViewModel = mavericksViewModel()) {
    val places by viewModel.collectAsState(PlacesState::places)
    val editState by viewModel.collectAsState(PlacesState::editState)
    val searchState by viewModel.collectAsState(PlacesState::searchState)

    PlacesScreenContent(
        places = places,
        editState = editState,
        searchState = searchState,
        onSearchStateChanged = viewModel::setSearchState,
        onEditStateChanged = viewModel::setEditState,
    )
}

@Composable
fun PlacesScreenContent(
    places: List<PlaceEntity>,
    editState: EditState = EditState.Done,
    searchState: SearchState = SearchState.Inactive,
    onSearchStateChanged: (SearchState) -> Unit,
    onEditStateChanged: (EditState) -> Unit,
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
            SearchState.Inactive -> {
                RulonaPlacesHeader(editState, onEditStateChanged)

                RulonaPlacesList(
                    places = places,
                    onItemClick = { Log.e("TAG", "Clicked") },
                    editState = editState,
                )
            }
            SearchState.Active -> {
                RulonaSearchHeader()
            }
            SearchState.Search -> {
                Text("Searching...")
            }
        }
    }
}