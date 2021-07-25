package de.thb.ui.screens.route

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.location.LocationRequest
import com.google.maps.model.EncodedPolyline
import de.thb.core.data.sources.location.LocationDataSourceImpl
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.data.sources.rules.RulesRepository
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.rule.RuleWithCategoryEntity
import de.thb.core.manager.RouteManager
import de.thb.core.util.MapLatLng
import de.thb.core.util.RuleUtils
import de.thb.ui.components.MapView
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.RulonaSearchBarFilled
import de.thb.ui.components.places.RulonaCategoryWithRules
import de.thb.ui.screens.route.RouteScreenUseCase.OpenPlaceDetailsUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.RequestLocationUpdatesUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.SearchUseCase
import de.thb.ui.screens.route.RouteUiState.OverviewUiState
import de.thb.ui.screens.route.RouteUiState.PlaceDetailsUiState
import de.thb.ui.screens.route.RouteUiState.SearchUiState
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.type.EditState
import de.thb.ui.type.RulonaAppBarAction.Back
import de.thb.ui.type.SearchState
import de.thb.ui.util.rememberMapViewWithLifecycle
import de.thb.ui.util.state
import de.thb.ui.util.toLatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class RouteUiState {
    data class SearchUiState(
        val query: String = "",
        val location: MapLatLng? = null,
        val searchedPlaces: List<PlaceEntity> = listOf(),
    ) : RouteUiState()

    data class OverviewUiState(
        val location: MapLatLng? = null,
    ) : RouteUiState()

    data class PlaceDetailsUiState(
        val place: PlaceEntity,
        val placeLocation: MapLatLng? = null,
        val polyline: EncodedPolyline? = null,
        val rules: List<RuleWithCategoryEntity> = listOf(),
        val location: MapLatLng? = null,
    ) : RouteUiState()
}

data class RouteState(
    val uiState: RouteUiState = OverviewUiState(),
) : MavericksState

class RouteViewModel(
    initialState: RouteState,
) : MavericksViewModel<RouteState>(initialState), KoinComponent {

    companion object {
        const val TAG = "RouteViewModel"
    }

    private var loadRulesJob: Job? = null

    private val placesRepository by inject<PlacesRepository>()
    private val rulesRepository by inject<RulesRepository>()
    private val routeManager by inject<RouteManager>()

    init {
        stateFlow.combine(placesRepository.getAll()) { state, places ->
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

        onEach { state ->
            when (val uiState = state.uiState) {
                is PlaceDetailsUiState -> {
                    if (uiState.placeLocation == null) {
                        routeManager.getLatLngByName(uiState.place.name)
                            ?.let { latLng ->
                                setState { copy(uiState = uiState.copy(placeLocation = latLng)) }
                            }
                    }

                    if (uiState.polyline == null) {
                        setPolylineForRoute()
                    }
                }
            }
        }
    }

    fun action(useCase: RouteScreenUseCase) {
        when (useCase) {
            is OpenPlaceDetailsUseCase -> setState { copy(uiState = PlaceDetailsUiState(place = useCase.place)) }
            is RequestLocationUpdatesUseCase -> requestLocationUpdates(useCase.context)
            is SearchUseCase -> setScreenSearchState(useCase.searchState)
        }
    }

    private fun setPolylineForRoute() {
        withState { state ->
            when (val uiState = state.uiState) {
                is PlaceDetailsUiState -> {
                    if (uiState.placeLocation != null && uiState.location != null) {
                        val start = uiState.location
                        val end = uiState.placeLocation

                        viewModelScope.launch {
                            val polyline = routeManager.getDirectionsPolyline(
                                startLatLng = start.toLatLng(),
                                endLatLng = end.toLatLng(),
                            )

                            if (polyline != null) {

                                setState { state.copy(uiState = uiState.copy(polyline = polyline)) }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun requestLocationUpdates(context: Context) {
        val locationRepository = LocationDataSourceImpl.getInstance(context)

        viewModelScope.launch {
            locationRepository
                .requestLocationUpdates(LocationRequest.create())
                .catch { Log.e("Error", "${it.message}") }
                .collect { setLocationState(it) }
        }
    }

    private fun setLocationState(location: MapLatLng?) {
        withState { state ->
            when (val uiState = state.uiState) {
                is PlaceDetailsUiState -> setState { copy(uiState = uiState.copy(location = location)) }
                is SearchUiState -> setState { copy(uiState = uiState.copy(location = location)) }
                is OverviewUiState -> setState { copy(uiState = uiState.copy(location = location)) }
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

    fun loadRules(placeId: String) {
        // make sure to only have a single job
        // active to update the state
        loadRulesJob?.let { job ->
            if (!job.isCancelled) job.cancel()
        }

        loadRulesJob = stateFlow
            .combine(rulesRepository.getByPlaceId(placeId)) { state, rules ->
                when (val uiState = state.uiState) {
                    is PlaceDetailsUiState -> {
                        if (rules.isNotEmpty()) {
                            setState { copy(uiState = uiState.copy(rules = rules)) }
                        }
                    }
                }
            }.launchIn(viewModelScope)
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

    SideEffect {
        // not necessary to check permission, as callback
        // gets triggered with isGranted = true
        // if already provided.
        requestLocationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
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
                SideEffect { viewModel.loadRules(uiState.place.id) }

                searchBarVisible = false

                PlaceDetailsScreen(
                    place = uiState.place,
                    placeLocation = uiState.placeLocation,
                    polyline = uiState.polyline,
                    rules = uiState.rules,
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
                RulonaSearchBarFilled(
                    onSearchStateChanged = { searchState ->
                        viewModel.action(SearchUseCase(searchState))
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
            PlaceSearchRouteItem(onPlaceClicked, place)
        }
    }
}

@Composable
private fun PlaceSearchRouteItem(
    onPlaceClicked: (PlaceEntity) -> Unit,
    place: PlaceEntity
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onPlaceClicked(place) }
            .padding(vertical = margin_medium, horizontal = margin_large)
    ) {
        Text(place.name)
    }
}

@Composable
private fun PlacesOverviewScreen(location: MapLatLng?) {
    val mapView = rememberMapViewWithLifecycle()
    MapView(mapView, LocalContext.current, location)
}

@Composable
private fun PlaceDetailsScreen(
    place: PlaceEntity,
    placeLocation: MapLatLng?,
    polyline: EncodedPolyline?,
    rules: List<RuleWithCategoryEntity>,
    onBackClicked: () -> Unit
) {
    val rulesWithCategoriesGrouped = remember(rules) {
        RuleUtils.groupRulesByCategory(rules)
    }

    val mapView = rememberMapViewWithLifecycle()

    Box(Modifier.fillMaxSize()) {
        Column {
            RulonaAppBar(
                title = place.name,
                back = Back { onBackClicked() }
            )

            MapView(mapView, LocalContext.current, placeLocation, polyline)
        }

        var expanded by remember { mutableStateOf(false) }
        val rotation by animateFloatAsState(
            if (expanded) -90f else 90f
        )

        Column(
            Modifier
                .fillMaxWidth()
                .background(MaterialTheme.colors.surface)
                .align(Alignment.BottomCenter)
        ) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .clickable { expanded = !expanded }
                    .padding(margin_medium)
            ) {
                Text(
                    text = "Regeln der Landkreise",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(0.9f)
                )

                Image(
                    imageVector = Icons.Default.ChevronLeft,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation),
                    alignment = Alignment.CenterEnd,
                )
            }

            AnimatedVisibility(expanded) {
                LazyColumn {
                    items(rulesWithCategoriesGrouped) { rule ->
                        RulonaCategoryWithRules(
                            categoryWithRules = rule,
                            editState = EditState.Done(),
                            onItemRemoved = {},
                            onItemAdded = {},
                        )
                    }
                }
            }
        }
    }
}
