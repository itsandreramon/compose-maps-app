package de.thb.ui.screens.route

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.focusTarget
import androidx.compose.ui.platform.LocalContext
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.location.LocationRequest
import de.thb.core.data.location.LocationDataSourceImpl
import de.thb.core.domain.PlaceEntity
import de.thb.ui.components.RulonaSearchBar
import de.thb.ui.screens.route.RouteScreenUiState.PlaceDetailsUiState
import de.thb.ui.screens.route.RouteScreenUiState.SearchUiState
import de.thb.ui.screens.route.RouteScreenUseCase.OpenPlaceDetailsUseCase
import de.thb.ui.screens.route.RouteScreenUseCase.RequestLocationUpdatesUseCase
import de.thb.ui.theme.margin_medium
import de.thb.ui.util.hasLocationPermission
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent

sealed class RouteScreenUiState {
    data class SearchUiState(
        val query: String = "",
        val location: Location? = null,
    ) : RouteScreenUiState()

    data class PlaceDetailsUiState(
        val place: PlaceEntity,
        val location: Location? = null,
    ) : RouteScreenUiState()
}

data class RouteState(
    val uiState: RouteScreenUiState = SearchUiState(),
) : MavericksState

class RouteViewModel(
    initialState: RouteState,
) : MavericksViewModel<RouteState>(initialState), KoinComponent {

    fun action(useCase: RouteScreenUseCase) {
        when (useCase) {
            is RequestLocationUpdatesUseCase -> requestLocationUpdates(useCase.context)
            is OpenPlaceDetailsUseCase -> {
                // ...
            }
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

    Column(
        Modifier
            .statusBarsPadding()
            .padding(margin_medium)
            .focusRequester(focusRequester)
            .focusTarget()
    ) {
        when (val uiState = routeUiState.uiState) {
            is SearchUiState -> {
                PlacesSearchScreen(
                    location = uiState.location,
                    query = uiState.query,
                    onFocusRequested = { focusRequester.requestFocus() }
                )
            }
        }
    }
}

@Composable
private fun PlacesSearchScreen(
    location: Location?,
    query: String,
    onFocusRequested: () -> Unit,
) {
    Column {
        RulonaSearchBar(
            onSearchStateChanged = {},
            onFocusRequested = onFocusRequested,
        )
    }
}
