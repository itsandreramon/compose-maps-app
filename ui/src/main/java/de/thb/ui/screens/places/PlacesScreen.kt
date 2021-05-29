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
import de.thb.ui.components.RulonaPlacesList
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.components.ScreenTitle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class PlacesState(
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
}

@Composable
fun PlacesScreen(viewModel: PlacesViewModel = mavericksViewModel()) {
    val places by viewModel.collectAsState(PlacesState::places)
    PlacesScreenContent(places)
}

@Composable
fun PlacesScreenContent(places: List<PlaceEntity>) {
    Column(
        Modifier
            .statusBarsPadding()
            .padding(16.dp)
    ) {
        ScreenTitle(title = "Places", Modifier.padding(vertical = 16.dp))
        RulonaSearchBar(Modifier.padding(bottom = 8.dp))
        RulonaPlacesList(places) { Log.e("TAG", "Clicked") }
    }
}
