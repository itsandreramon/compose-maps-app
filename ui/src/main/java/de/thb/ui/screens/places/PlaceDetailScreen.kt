package de.thb.ui.screens.places

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.domain.Filter
import de.thb.core.domain.PlaceEntity
import de.thb.core.domain.Severity
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.components.places.RulonaFilter
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.rulona_material_red_600
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
            back = Back(onBackClicked),
            actions = listOf(
                Notify(onNotifyClicked),
                Share(onShareClicked)
            )
        )

        Column(
            modifier = Modifier
                .padding(top = margin_large)
                .padding(horizontal = margin_medium)
        ) {
            var editState: EditState by state { EditState.Done() }

            Column(Modifier.padding(margin_medium)) {
                Row {
                    Text(text = "Inzidenz")

                    Image(
                        imageVector = Icons.Default.ArrowDropUp,
                        colorFilter = ColorFilter.tint(rulona_material_red_600),
                        contentDescription = null
                    )

                    Text(text = "${place.incidence}")
                }

                Text(
                    text = buildAnnotatedString {
                        append("Die offiziellen Regeln für ${place.name} lassen sich ")
                        withStyle(style = SpanStyle(textDecoration = TextDecoration.Underline)) {
                            append("hier")
                        }
                        append(" einsehen.")
                    },
                    modifier = Modifier.padding(top = margin_medium)
                )
            }

            RulonaHeaderEditable(
                title = "Mein Filter",
                editState = editState,
                onEditStateChanged = { state -> editState = state }
            )

            RulonaFilter(
                filter = Filter("Restaurants", Severity.RED),
                editState = editState,
                onItemRemoved = {}
            )

            RulonaFilter(
                filter = Filter("Bars", Severity.YELLOW),
                editState = editState,
                onItemRemoved = {}
            )

            RulonaFilter(
                filter = Filter("Biergärten", Severity.GREEN),
                editState = editState,
                onItemRemoved = {}
            )
        }
    }
}
