package de.thb.ui.screens.places

import android.Manifest
import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.AlertDialog
import androidx.compose.material.Text
import androidx.compose.material.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalContext
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import de.thb.core.data.sources.location.LocationDataSourceImpl
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.manager.RouteManager
import de.thb.core.util.fromUtc
import de.thb.ui.components.RulonaHeaderEditable
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.components.ScreenTitle
import de.thb.ui.components.places.RulonaPlacesList
import de.thb.ui.components.search.RulonaSearchHeader
import de.thb.ui.components.search.RulonaSearchList
import de.thb.ui.screens.places.PlacesScreenUseCase.EditBookmarksUseCase
import de.thb.ui.screens.places.PlacesScreenUseCase.SearchCurrentLocationUseCase
import de.thb.ui.screens.places.PlacesScreenUseCase.SearchUseCase
import de.thb.ui.screens.places.PlacesScreenUseCase.SetPlaceSearchTimestampUseCase
import de.thb.ui.screens.places.PlacesScreenUseCase.TogglePlaceBookmarkUseCase
import de.thb.ui.screens.places.PlacesUiState.BookmarksUiState
import de.thb.ui.screens.places.PlacesUiState.EditBookmarksUiState
import de.thb.ui.screens.places.PlacesUiState.RecentlySearchedUiState
import de.thb.ui.screens.places.PlacesUiState.SearchUiState
import de.thb.ui.theme.margin_medium
import de.thb.ui.type.EditState
import de.thb.ui.type.SearchState
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import java.time.Instant

sealed class PlacesUiState {
    data class RecentlySearchedUiState(
        val recentlySearchedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    data class SearchUiState(
        val query: String,
        val currentlySearchedPlaces: List<PlaceEntity> = listOf(),
    ) : PlacesUiState()

    object BookmarksUiState : PlacesUiState()

    object EditBookmarksUiState : PlacesUiState()
}

data class PlacesState(
    val uiState: PlacesUiState = BookmarksUiState,
    val isLoadingCurrentLocation: Boolean = false,
    val currentLocationPlaceId: String? = null,
    val bookmarkedPlaces: List<PlaceEntity> = listOf(),
) : MavericksState

class PlacesViewModel(
    initialState: PlacesState,
) : MavericksViewModel<PlacesState>(initialState), KoinComponent {

    private val placesRepository by inject<PlacesRepository>()
    private val routeManager by inject<RouteManager>()

    init {
        placesRepository.getAll()
            .onEach { places ->
                val bookmarkedPlaces = places
                    .filter { it.isBookmarked }
                    .sortedBy { it.name }

                setState { copy(bookmarkedPlaces = bookmarkedPlaces) }
            }
            .launchIn(viewModelScope)

        stateFlow.combine(placesRepository.getAll()) { state, places ->
            when (val uiState = state.uiState) {
                is RecentlySearchedUiState -> {
                    val recentlySearchedPlaces = places
                        .filter { it.searchedAtUtc != null }
                        .sortedByDescending { fromUtc(it.searchedAtUtc!!) }

                    setState { copy(uiState = uiState.copy(recentlySearchedPlaces)) }
                }
                is SearchUiState -> {
                    val searchedPlaces = places.filter {
                        it.name.contains(uiState.query, ignoreCase = true)
                    }

                    setState { copy(uiState = uiState.copy(uiState.query, searchedPlaces)) }
                }
            }
        }.launchIn(viewModelScope)
    }

    fun action(useCase: PlacesScreenUseCase) {
        when (useCase) {
            is EditBookmarksUseCase -> setScreenEditState(useCase.editState)
            is SearchUseCase -> setScreenSearchState(useCase.searchState)
            is SearchCurrentLocationUseCase -> setSearchCurrentLocationState(useCase.context)
            is TogglePlaceBookmarkUseCase -> togglePlaceItemBookmark(useCase.place)
            is SetPlaceSearchTimestampUseCase -> setPlaceSearchedTimestamp(useCase.place)
        }
    }

    private fun setSearchCurrentLocationState(context: Context) {
        setState { copy(isLoadingCurrentLocation = true) }
        resolveLocation(context)
    }

    private fun resolveLocation(context: Context) {
        val locationDataSource = LocationDataSourceImpl.getInstance(context)
        viewModelScope.launch {
            val districtId = routeManager.getPlaceIdByLatLng(
                currLocation = locationDataSource.getLastLocation().first()
            )

            setState {
                copy(
                    currentLocationPlaceId = districtId,
                    isLoadingCurrentLocation = false
                )
            }
        }
    }

    private fun setScreenEditState(state: EditState) {
        when (state) {
            is EditState.Editing -> setState { copy(uiState = EditBookmarksUiState) }
            else -> setState { copy(uiState = BookmarksUiState) }
        }
    }

    private fun setScreenSearchState(state: SearchState) {
        when (state) {
            is SearchState.Active -> setState { copy(uiState = RecentlySearchedUiState()) }
            is SearchState.Inactive -> setState { copy(uiState = BookmarksUiState) }
            is SearchState.Search -> setState { copy(uiState = SearchUiState(state.query)) }
        }
    }

    private fun togglePlaceItemBookmark(place: PlaceEntity) {
        viewModelScope.launch {
            val updatedPlace = place.copy(isBookmarked = !place.isBookmarked)
            placesRepository.insert(updatedPlace)
        }
    }

    private fun setPlaceSearchedTimestamp(place: PlaceEntity) {
        viewModelScope.launch {
            val updatedPlace = place.copy(searchedAtUtc = Instant.now().toString())
            Log.e("VM", "setting searched at..")
            placesRepository.insert(updatedPlace)
        }
    }
}

@Composable
fun PlacesScreen(
    viewModel: PlacesViewModel = mavericksViewModel(),
    onPlaceLoaded: (id: String) -> Unit
) {
    val placesUiState = viewModel.collectAsState(PlacesState::uiState)
    val bookmarkedPlaces by viewModel.collectAsState(PlacesState::bookmarkedPlaces)
    val isLoadingCurrentLocation by viewModel.collectAsState(PlacesState::isLoadingCurrentLocation)
    val currentLocationPlaceId by viewModel.collectAsState(PlacesState::currentLocationPlaceId)

    val focusRequester = FocusRequester()
    val context = LocalContext.current

    LaunchedEffect(currentLocationPlaceId) {
        currentLocationPlaceId?.let(onPlaceLoaded)
    }

    if (isLoadingCurrentLocation) {
        AlertDialog(
            title = { Text("Bitte warten...") },
            text = { Text(text = "Suche nach deinem aktuellen Landkreis.") },
            onDismissRequest = {},
            dismissButton = { TextButton(onClick = {}) { Text(text = "Abbruch") } },
            confirmButton = {},
        )
    }

    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.action(SearchCurrentLocationUseCase(context))
            }
        }

    Column(
        Modifier
            .statusBarsPadding()
            .padding(margin_medium)
            .focusRequester(focusRequester)
            .focusTarget()
    ) {
        ScreenTitle(title = "Orte", Modifier.padding(vertical = margin_medium))

        RulonaSearchBar(
            onSearchStateChanged = { searchState ->
                viewModel.action(SearchUseCase(searchState))
            },
            onCurrentLocationClicked = {
                requestLocationPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            },
            onFocusRequested = { focusRequester.requestFocus() },
        )

        when (val uiState = placesUiState.value) {
            is BookmarksUiState -> {
                PlacesBookmarks(
                    bookmarkedPlaces = bookmarkedPlaces,
                    onEditStateChanged = { viewModel.action(EditBookmarksUseCase(it)) },
                    onPlaceClicked = { place -> onPlaceLoaded(place.id) },
                )
            }
            is EditBookmarksUiState -> {
                PlacesEditBookmarks(
                    bookmarkedPlaces = bookmarkedPlaces,
                    onEditStateChanged = { editState ->
                        viewModel.action(EditBookmarksUseCase(editState))
                    },
                    onItemRemoveClicked = { place ->
                        viewModel.action(TogglePlaceBookmarkUseCase(place))
                    },
                )
            }
            is RecentlySearchedUiState -> {
                PlacesRecentlySearched(
                    recentlySearchedPlaces = uiState.recentlySearchedPlaces,
                    onItemBookmarkClicked = { place ->
                        viewModel.action(TogglePlaceBookmarkUseCase(place))
                    },
                    onPlaceClicked = { place -> onPlaceLoaded(place.id) },
                )
            }
            is SearchUiState -> {
                PlacesSearch(
                    currentlySearchedPlaces = uiState.currentlySearchedPlaces.sortedBy { it.name },
                    onItemBookmarkClicked = { place ->
                        viewModel.action(TogglePlaceBookmarkUseCase(place))
                    },
                    onPlaceSearched = { place ->
                        viewModel.action(SetPlaceSearchTimestampUseCase(place))
                    },
                    onPlaceClicked = { place -> onPlaceLoaded(place.id) },
                )
            }
        }
    }
}

@Composable
fun PlacesSearch(
    currentlySearchedPlaces: List<PlaceEntity>,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
    onPlaceSearched: (PlaceEntity) -> Unit,
    onPlaceClicked: (PlaceEntity) -> Unit,
) {
    if (currentlySearchedPlaces.isNotEmpty()) {
        RulonaSearchList(
            places = currentlySearchedPlaces,
            onItemClick = { id ->
                onPlaceClicked(id)
                onPlaceSearched(id)
            },
            onItemBookmarkClicked = {
                onItemBookmarkClicked(it)
            },
        )
    }
}

@Composable
fun PlacesRecentlySearched(
    recentlySearchedPlaces: List<PlaceEntity>,
    onItemBookmarkClicked: (PlaceEntity) -> Unit,
    onPlaceClicked: (PlaceEntity) -> Unit,
) {
    RulonaSearchHeader()

    if (recentlySearchedPlaces.isNotEmpty()) {
        RulonaSearchList(
            places = recentlySearchedPlaces,
            onItemClick = onPlaceClicked,
            onItemBookmarkClicked = onItemBookmarkClicked
        )
    }
}

@Composable
fun PlacesBookmarks(
    bookmarkedPlaces: List<PlaceEntity>,
    onEditStateChanged: (EditState) -> Unit,
    onPlaceClicked: (PlaceEntity) -> Unit,
) {
    RulonaHeaderEditable(
        title = "Meine Orte",
        editState = EditState.Done(),
        onEditStateChanged = onEditStateChanged
    )

    val alpha by animateFloatAsState(
        targetValue = if (bookmarkedPlaces.isNotEmpty()) 1f else 0f,
        animationSpec = tween(500)
    )

    Box(Modifier.alpha(alpha)) {
        RulonaPlacesList(
            places = bookmarkedPlaces,
            editState = EditState.Done(),
            onItemClick = onPlaceClicked,
        )
    }
}

@Composable
fun PlacesEditBookmarks(
    bookmarkedPlaces: List<PlaceEntity>,
    onEditStateChanged: (EditState) -> Unit,
    onItemRemoveClicked: (PlaceEntity) -> Unit,
) {
    RulonaHeaderEditable(
        title = "Meine Orte",
        editState = EditState.Editing(),
        onEditStateChanged = onEditStateChanged
    )

    if (bookmarkedPlaces.isNotEmpty()) {
        RulonaPlacesList(
            places = bookmarkedPlaces,
            editState = EditState.Editing(),
            onItemRemoved = onItemRemoveClicked,
        )
    }
}
