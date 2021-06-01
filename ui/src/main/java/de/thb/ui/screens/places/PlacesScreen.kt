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
import de.thb.ui.components.RulonaPlacesHeader
import de.thb.ui.components.RulonaPlacesList
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.components.ScreenTitle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class PlacesState(
    val isInEditMode: Boolean = false,
    val isInSearchMode: Boolean = false,
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

    fun setEditMode(editMode: Boolean) {
        setState { copy(isInEditMode = editMode) }
    }

    fun setSearchMode(searchMode: Boolean) {
        setState { copy(isInSearchMode = searchMode) }
    }
}

@Composable
fun PlacesScreen(viewModel: PlacesViewModel = mavericksViewModel()) {
    val places by viewModel.collectAsState(PlacesState::places)
    val isInEditMode by viewModel.collectAsState(PlacesState::isInEditMode)
    val isInSearchMode by viewModel.collectAsState(PlacesState::isInSearchMode)

    PlacesScreenContent(
        places = places,
        isInEditMode = isInEditMode,
        isInSearchMode = isInSearchMode,
        onSearchStateChanged = { mode -> viewModel.setSearchMode(mode) },
        onEditClicked = { viewModel.setEditMode(true) },
        onCloseClicked = { viewModel.setEditMode(false) },
    )
}

@Composable
fun PlacesScreenContent(
    places: List<PlaceEntity>,
    isInEditMode: Boolean = false,
    isInSearchMode: Boolean = false,
    onSearchStateChanged: (Boolean) -> Unit,
    onEditClicked: () -> Unit,
    onCloseClicked: () -> Unit,
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

        RulonaPlacesHeader(onEditClicked, onCloseClicked, isInEditMode)

        if (isInSearchMode) {
            Text(text = "Searching...")
        } else {
            RulonaPlacesList(
                places = places,
                onItemClick = { Log.e("TAG", "Clicked") },
                isInEditMode = isInEditMode,
            )
        }
    }
}