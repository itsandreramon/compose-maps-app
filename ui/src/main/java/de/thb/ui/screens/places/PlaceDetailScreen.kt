package de.thb.ui.screens.places

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.domain.PlaceEntity
import de.thb.core.domain.Severity
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.components.places.RulonaPlaceCategory
import de.thb.ui.theme.margin_medium
import de.thb.ui.type.EditState
import de.thb.ui.type.RulonaAppBarAction.Back
import de.thb.ui.type.RulonaAppBarAction.Notify
import de.thb.ui.type.RulonaAppBarAction.Share
import de.thb.ui.util.state
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

data class PlaceDetailsState(
    val placeUuid: String? = null,
    val place: PlaceEntity? = null,
) : MavericksState

class PlaceDetailsViewModel(
    initialState: PlaceDetailsState,
) : MavericksViewModel<PlaceDetailsState>(initialState), KoinComponent {

    private val placesLocalDataSource by inject<PlacesLocalDataSource>()

    init {
        viewModelScope.launch {
            stateFlow.filter { it.placeUuid != null }.flatMapLatest { state ->
                placesLocalDataSource.getByUuid(state.placeUuid!!)
            }.collect { setState { copy(place = it) } }
        }
    }

    fun setPlaceUuid(uuid: String) {
        setState { copy(placeUuid = uuid) }
    }
}

@Composable
fun PlaceDetailsScreen(
    placeUuid: String,
    onBackClicked: () -> Unit,
    viewModel: PlaceDetailsViewModel = mavericksViewModel()
) {
    val place by viewModel.collectAsState(PlaceDetailsState::place)

    LaunchedEffect(placeUuid) {
        viewModel.setPlaceUuid(placeUuid)
    }

    place?.let {
        PlaceDetailsScreenContent(
            place = it,
            onBackClicked = onBackClicked,
            onNotifyClicked = {},
            onShareClicked = {},
        )
    }
}

@Composable
fun PlaceDetailsScreenContent(
    place: PlaceEntity,
    onBackClicked: () -> Unit,
    onNotifyClicked: () -> Unit,
    onShareClicked: () -> Unit,
) {
    Column {
        RulonaAppBar(
            title = place.name,
            back = Back(onClick = onBackClicked),
            actions = listOf(
                Notify(onClick = onNotifyClicked),
                Share(onClick = onShareClicked)
            )
        )

        Column(Modifier.padding(horizontal = margin_medium)) {
            var editState: EditState by state { EditState.Done() }

            RulonaHeaderEditable(
                title = "Mein Filter",
                editState = editState,
                onEditStateChanged = { state -> editState = state }
            )

            RulonaPlaceCategory(name = "Restaurants", Severity.RED)
            RulonaPlaceCategory(name = "Bars", Severity.YELLOW)
            RulonaPlaceCategory(name = "Biergärten", Severity.GREEN)
        }
    }
}
