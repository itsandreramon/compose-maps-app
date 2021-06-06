package de.thb.ui.screens.route

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.location.LocationRequest
import de.thb.core.data.location.LocationDataSourceImpl
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.domain.PlaceEntity
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.screens.route.RouteScreenUseCase.OpenPlaceDetailsUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.RequestLocationUpdatesUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.SearchUseCase
import de.thb.ui.screens.route.RouteUiState.OverviewUiState
import de.thb.ui.screens.route.RouteUiState.PlaceDetailsUiState
import de.thb.ui.screens.route.RouteUiState.SearchUiState
import de.thb.ui.theme.margin_medium
import de.thb.ui.type.RulonaAppBarAction.Back
import de.thb.ui.type.SearchState
import de.thb.ui.util.hasLocationPermission
import de.thb.ui.util.state
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class RouteUiState {
    data class SearchUiState(
        val query: String = "",
        val location: Location? = null,
        val searchedPlaces: List<PlaceEntity> = listOf(),
    ) : RouteUiState()

    data class OverviewUiState(
        val location: Location? = null,
    ) : RouteUiState()

    data class PlaceDetailsUiState(
        val place: PlaceEntity,
        val location: Location? = null,
    ) : RouteUiState()
}

data class RouteState(
    val uiState: RouteUiState = OverviewUiState(),
) : MavericksState

class RouteViewModel(
    initialState: RouteState,
) : MavericksViewModel<RouteState>(initialState), KoinComponent {

    private val placesLocalDataSource by inject<PlacesLocalDataSource>()

    init {
        stateFlow.combine(placesLocalDataSource.getAll()) { state, places ->
            when (val uiState = state.uiState) {
                is SearchUiState -> {
                    val searchedPlaces = if (uiState.query.isNotBlank()) {
                        places.filter { it.name.contains(uiState.query, ignoreCase = true) }
                    } else listOf()

                    setState {
                        copy(
                            uiState = uiState.copy(
                                query = uiState.query,
                                searchedPlaces = searchedPlaces
                            )
                        )
                    }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: RouteScreenUseCase) {
        when (useCase) {
            is OpenPlaceDetailsUseCase -> setState { copy(uiState = PlaceDetailsUiState(place = useCase.place)) }
            is RequestLocationUpdatesUseCase -> requestLocationUpdates(useCase.context)
            is SearchUseCase -> setScreenSearchState(useCase.searchState)
        }
    }

    private fun requestLocationUpdates(context: Context) {
        val locationRepository = LocationDataSourceImpl.getInstance(context)

        viewModelScope.launch {
            locationRepository
                .requestLocationUpdates(LocationRequest.create())
                .sample(periodMillis = 1000)
                .catch { Log.e("Error", "${it.message}") }
                .collect { setLocationState(it) }
        }
    }

    private fun setLocationState(location: Location?) {
        withState { state ->
            when (val uiState = state.uiState) {
                is PlaceDetailsUiState -> setState { copy(uiState = uiState.copy(location = location)) }
                is SearchUiState -> setState { copy(uiState = uiState.copy(location = location)) }
            }
        }
    }

    private fun setScreenSearchState(state: SearchState) {
        when (state) {
            is SearchState.Active -> setState {
                // not necessary to copy old state as
                // database only emits on changes, else
                // existing data is used in init {}
                copy(uiState = SearchUiState(state.query))
            }
            is SearchState.Search -> setState {
                copy(uiState = SearchUiState(state.query))
            }
            is SearchState.Inactive -> setState {
                copy(uiState = OverviewUiState())
            }
        }
    }
}

@Composable
fun RouteScreen(viewModel: RouteViewModel = mavericksViewModel()) {
    val context = LocalContext.current

    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.action(RequestLocationUpdatesUseCase(context))
            }
        }

    if (hasLocationPermission(context)) {
        viewModel.action(RequestLocationUpdatesUseCase(context))
    } else {
        SideEffect {
            requestLocationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    val routeUiState by viewModel.collectAsState()
    val focusRequester = FocusRequester()

    var searchBarVisible by state { false }

    Box(
        Modifier
            .focusRequester(focusRequester)
            .focusTarget()
    ) {
        when (val uiState = routeUiState.uiState) {
            is OverviewUiState -> {
                searchBarVisible = true

                PlacesOverviewScreen(uiState.location)
            }
            is SearchUiState -> {
                searchBarVisible = true

                PlacesSearchScreen(
                    currentlySearchedPlaces = uiState.searchedPlaces,
                    onPlaceClicked = { place ->
                        viewModel.action(OpenPlaceDetailsUseCase(place))
                    },
                )
            }
            is PlaceDetailsUiState -> {
                searchBarVisible = false

                PlaceDetailsScreen(
                    place = uiState.place,
                    onBackClicked = { viewModel.action(SearchUseCase(SearchState.Inactive())) }
                )
            }
        }

        if (searchBarVisible) {
            Box(
                Modifier
                    .statusBarsPadding()
                    .padding(horizontal = margin_medium)
            ) {
                RulonaSearchBar(
                    onSearchStateChanged = { searchState ->
                        viewModel.action(
                            SearchUseCase(
                                searchState
                            )
                        )
                    },
                    onFocusRequested = { focusRequester.requestFocus() },
                )
            }
        }
    }
}

@Composable
private fun PlacesSearchScreen(
    currentlySearchedPlaces: List<PlaceEntity>,
    onPlaceClicked: (PlaceEntity) -> Unit,
) {
    LazyColumn(
        Modifier
            .statusBarsPadding()
            .padding(top = 84.dp)
    ) {
        items(currentlySearchedPlaces) { place ->
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable {
                        onPlaceClicked(place)
                    }
                    .padding(margin_medium)
            ) {
                Text(place.name)
            }
        }
    }
}

@Composable
private fun PlacesOverviewScreen(location: Location?) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Column(Modifier.align(Alignment.Center)) {
            Text("Google Map")
            Text("Location: $location")
        }
    }
}

@Composable
private fun PlaceDetailsScreen(place: PlaceEntity, onBackClicked: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color.Blue)
    ) {
        Column {
            RulonaAppBar(
                title = place.name,
                back = Back { onBackClicked() }
            )

            Box(Modifier.fillMaxSize()) {
                Text("Google Map", modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}
