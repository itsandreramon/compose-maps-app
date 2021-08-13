package de.thb.ui.screens.route

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.AlertDialog
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.TextButton
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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.LocalWindowInsets
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.location.LocationRequest
import de.thb.core.data.sources.location.LocationDataSourceImpl
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.data.sources.route.RouteRemoteDataSource
import de.thb.core.data.sources.rules.RulesRepository
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.route.type.Coordinate
import de.thb.core.domain.route.type.RestrictedPlace
import de.thb.core.domain.rule.RuleWithCategoryEntity
import de.thb.core.util.MapLatLng
import de.thb.core.util.RuleUtils
import de.thb.ui.components.MapView
import de.thb.ui.components.RulonaAppBar
import de.thb.ui.components.RulonaSearchBarFilled
import de.thb.ui.components.places.RulonaEmptySearchQueryLayout
import de.thb.ui.components.places.RulonaEmptySearchResultsLayout
import de.thb.ui.components.route.RulonaRouteRuleItem
import de.thb.ui.screens.route.RouteScreenUseCase.CancelLoadRouteInformation
import de.thb.ui.screens.route.RouteScreenUseCase.HideDialogUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.OpenPlaceDetailsUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.RequestLocationUpdatesUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.SearchUseCase
import de.thb.ui.screens.route.RouteUiState.OverviewUiState
import de.thb.ui.screens.route.RouteUiState.PlaceDetailsUiState
import de.thb.ui.screens.route.RouteUiState.SearchUiState
import de.thb.ui.theme.margin_large
import de.thb.ui.theme.margin_medium
import de.thb.ui.theme.margin_small
import de.thb.ui.type.DialogType
import de.thb.ui.type.RulonaAppBarAction.Back
import de.thb.ui.type.SearchState
import de.thb.ui.util.rememberMapViewWithLifecycle
import de.thb.ui.util.state
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

sealed class RouteUiState {
    data class SearchUiState(
        val location: MapLatLng? = null,
    ) : RouteUiState()

    data class OverviewUiState(
        val location: MapLatLng? = null,
    ) : RouteUiState()

    data class PlaceDetailsUiState(
        val place: PlaceEntity,
        val placeLocation: MapLatLng? = null,
        val restrictedPlaces: List<RestrictedPlace> = listOf(),
        val polyline: List<MapLatLng> = listOf(),
        val boundaries: List<List<MapLatLng>> = listOf(),
        val rulesInRoute: List<RuleWithCategoryEntity> = listOf(),
        val placesInRoute: List<PlaceEntity> = listOf(),
        val location: MapLatLng? = null,
    ) : RouteUiState()
}

data class RouteState(
    val query: String = "",
    val searchedPlaces: List<PlaceEntity> = listOf(),
    val isLoadingRouteInformation: Boolean = false,
    val isErrorLoadingRouteDialogVisible: Boolean = false,
    val uiState: RouteUiState = OverviewUiState(),
) : MavericksState

class RouteViewModel(
    initialState: RouteState,
) : MavericksViewModel<RouteState>(initialState), KoinComponent {

    companion object {
        const val TAG = "RouteViewModel"
    }

    private var loadRouteJob: Job? = null

    private val placesRepository by inject<PlacesRepository>()
    private val rulesRepository by inject<RulesRepository>()
    private val routeRemoteDataSource by inject<RouteRemoteDataSource>()

    init {
        stateFlow.combine(placesRepository.getAll()) { state, places ->
            when (val uiState = state.uiState) {
                is SearchUiState -> {
                    val searchedPlaces = if (state.query.isNotBlank()) {
                        places.filter { it.name.contains(state.query, ignoreCase = true) }
                    } else listOf()

                    setState {
                        copy(
                            query = query,
                            searchedPlaces = searchedPlaces
                        )
                    }
                }
                is PlaceDetailsUiState -> {
                    if (uiState.restrictedPlaces.isNotEmpty() && uiState.placesInRoute.isEmpty()) {
                        val filteredPlaces = places.filter { place ->
                            uiState.restrictedPlaces.any { restrictedPlace ->
                                restrictedPlace.placeId == place.id
                            }
                        }

                        setState {
                            copy(uiState = uiState.copy(placesInRoute = filteredPlaces))
                        }
                    }
                }
                else -> {
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: RouteScreenUseCase) {
        when (useCase) {
            is OpenPlaceDetailsUseCase -> {
                setState { copy(uiState = PlaceDetailsUiState(place = useCase.destinationPlace)) }
                loadRouteInformation(useCase.context, useCase.destinationPlace.id, useCase.onError)
            }
            is RequestLocationUpdatesUseCase -> requestLocationUpdates(useCase.context)
            is SearchUseCase -> setScreenSearchState(useCase.searchState)
            is CancelLoadRouteInformation -> {
                setScreenSearchState(SearchState.Inactive())
                setState { copy(isLoadingRouteInformation = false) }
            }
            is HideDialogUseCase -> hideDialog(useCase.dialogType)
        }
    }

    private fun hideDialog(dialogType: DialogType) {
        when (dialogType) {
            DialogType.ErrorLoadingRouteInformation -> setState {
                copy(
                    isErrorLoadingRouteDialogVisible = false
                )
            }
            else -> {
                // TODO
            }
        }
    }

    private fun loadRouteInformation(
        context: Context,
        destinationPlaceId: String,
        onError: () -> Unit,
    ) {
        setState { copy(isLoadingRouteInformation = true) }

        loadRouteJob?.let {
            if (!it.isCancelled) it.cancel()
        }

        loadRouteJob = viewModelScope.launch {
            val locationDataSource = LocationDataSourceImpl.getInstance(context)
            val currLocation = locationDataSource.getLastLocation().first()

            val resp = routeRemoteDataSource.getRoute(
                originLatLng = currLocation,
                destinationPlaceId = destinationPlaceId,
            )

            if (resp != null) {

                val boundaries = mutableListOf<List<Coordinate>>()
                val rules = mutableListOf<RuleWithCategoryEntity>()

                for (place in resp.restrictedPlaces) {

                    // TODO fix bug
                    val rulesForPlace = rulesRepository.getByPlaceId(place.placeId)
                    rules.addAll(rulesForPlace.first())
                }

                withState { state ->
                    when (val uiState = state.uiState) {
                        is PlaceDetailsUiState -> {
                            setState {
                                copy(
                                    uiState = uiState.copy(
                                        polyline = resp.route
                                            .flatten()
                                            .map { MapLatLng(it.lat, it.lng) },
                                        boundaries = boundaries.map { coordinates ->
                                            coordinates.map {
                                                MapLatLng(it.lat, it.lng)
                                            }
                                        },
                                        restrictedPlaces = resp.restrictedPlaces,
                                        rulesInRoute = rules,
                                    ),
                                    isLoadingRouteInformation = false,
                                )
                            }
                        }
                    }
                }
            } else {
                setState {
                    copy(
                        isLoadingRouteInformation = false,
                        isErrorLoadingRouteDialogVisible = true
                    )
                }
                onError()
            }
        }
    }

    private fun requestLocationUpdates(context: Context) {
        val locationDataSource = LocationDataSourceImpl.getInstance(context)

        viewModelScope.launch {
            locationDataSource
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
                copy(uiState = SearchUiState(), query = state.query)
            }
            is SearchState.Search -> setState {
                copy(uiState = SearchUiState(), query = state.query)
            }
            is SearchState.Inactive -> {
                    setState {
                        copy(uiState = OverviewUiState())
                    }
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

    SideEffect {
        // not necessary to check permission, as callback
        // gets triggered with isGranted = true
        // if already provided.
        requestLocationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
    }

    val routeUiState by viewModel.collectAsState()
    val isLoadingRouteInformation by viewModel.collectAsState(RouteState::isLoadingRouteInformation)
    val isErrorLoadingRouteDialogVisible by viewModel.collectAsState(RouteState::isErrorLoadingRouteDialogVisible)
    val focusRequester = FocusRequester()

    var searchBarVisible by state { false }

    if (isLoadingRouteInformation) {
        AlertDialog(
            title = { Text("Bitte warten...") },
            text = { Text(text = "Suche nach Corona Beschränkungen auf der Route...") },
            onDismissRequest = {},
            dismissButton = {
                TextButton(onClick = {
                    viewModel.action(CancelLoadRouteInformation)
                }) { Text(text = "Abbruch") }
            },
            confirmButton = {},
        )
    }

    if (isErrorLoadingRouteDialogVisible) {
        AlertDialog(
            title = { Text("Fehler") },
            text = { Text(text = "Es konnte keine Route gefunden werden. Bitte versichere, dass du eine aktive Internet-Verbindung hast.") },
            onDismissRequest = {
                viewModel.action(HideDialogUseCase(DialogType.ErrorLoadingRouteInformation))
            },
            dismissButton = {
                TextButton(onClick = {
                    viewModel.action(CancelLoadRouteInformation)
                    viewModel.action(HideDialogUseCase(DialogType.ErrorLoadingRouteInformation))
                }) { Text(text = "Ok") }
            },
            confirmButton = {},
        )
    }

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

                if (routeUiState.query.isNotEmpty()) {
                    PlacesSearchScreen(
                        currentlySearchedPlaces = routeUiState.searchedPlaces,
                        onPlaceClicked = { place ->
                            viewModel.action(
                                OpenPlaceDetailsUseCase(place, context, onError = {
                                    viewModel.action(SearchUseCase(SearchState.Inactive()))
                                })
                            )
                        },
                    )
                } else {
                    RulonaEmptySearchQueryLayout()
                }
            }
            is PlaceDetailsUiState -> {
                searchBarVisible = false

                Log.e("TAG", "${uiState.rulesInRoute}")

                PlaceDetailsScreen(
                    place = uiState.place,
                    placeLocation = uiState.placeLocation,
                    boundaries = uiState.boundaries,
                    polyline = uiState.polyline,
                    rulesInRoute = uiState.rulesInRoute,
                    placesInRoute = uiState.placesInRoute,
                    onBackClicked = { viewModel.action(SearchUseCase(SearchState.Inactive())) }
                )
            }
        }

        if (searchBarVisible) {
            Column(
                Modifier
                    .statusBarsPadding()
                    .padding(horizontal = margin_medium)
            ) {
                RulonaSearchBarFilled(
                    onSearchStateChanged = { searchState ->
                        // fix route loading
                        if (!isLoadingRouteInformation) {
                            viewModel.action(SearchUseCase(searchState))
                        }
                    },
                    onFocusRequested = { focusRequester.requestFocus() },
                    hint = "Ziel"
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
    if (currentlySearchedPlaces.isNotEmpty()) {
        LazyColumn(
            Modifier
                .statusBarsPadding()
                .padding(top = 84.dp)
        ) {
            items(currentlySearchedPlaces) { place ->
                PlaceSearchRouteItem(onPlaceClicked, place)
            }
        }
    } else {
        RulonaEmptySearchResultsLayout()
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
    polyline: List<MapLatLng> = listOf(),
    boundaries: List<List<MapLatLng>> = listOf(),
    rulesInRoute: List<RuleWithCategoryEntity>,
    placesInRoute: List<PlaceEntity>,
    onBackClicked: () -> Unit,
) {
    val placesWithRulesGrouped = remember(placesInRoute, rulesInRoute) {
        RuleUtils.groupRulesByPlace(placesInRoute, rulesInRoute)
    }

    var expanded by remember { mutableStateOf(false) }
    val mapView = rememberMapViewWithLifecycle()

    Box(Modifier.fillMaxHeight()) {
        Column(modifier = Modifier.padding(bottom = 64.dp)) {
            RulonaAppBar(
                title = place.name,
                back = Back { onBackClicked() }
            )

            Box(modifier = Modifier) {
                MapView(mapView, LocalContext.current, placeLocation, polyline, boundaries)
            }
        }

        val statusBarPadding by animateDpAsState(
            with(LocalDensity.current) {
                if (expanded) {
                    LocalWindowInsets.current.statusBars.top.toDp()
                } else {
                    0.dp
                }
            }
        )

        Box(
            Modifier
                .align(Alignment.BottomCenter)
                .padding(top = statusBarPadding)
        ) {
            val rotation by animateFloatAsState(
                if (expanded) -90f else 90f
            )

            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colors.surface)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clickable { expanded = !expanded }
                        .padding(end = margin_medium, start = margin_small),
                ) {
                    Image(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        modifier = Modifier
                            .rotate(rotation)
                            .weight(0.1f)
                    )

                    Text(
                        text = "Regeln der Landkreise",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(start = margin_medium)
                    )
                }

                AnimatedVisibility(expanded) {
                    LazyColumn(Modifier.fillMaxSize()) {
                        items(placesWithRulesGrouped) { rule ->
                            RulonaRouteRuleItem(placeWithRules = rule)
                        }
                    }
                    Divider(Modifier.fillMaxWidth())
                }
            }
        }
    }
}
