package de.thb.ui.screens.route

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.Context
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalContext
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.android.gms.location.LocationRequest
import com.google.maps.GeoApiContext
import de.thb.core.data.location.LocationDataSourceImpl
import de.thb.ui.components.MapView
import de.thb.ui.screens.route.RouteScreenUseCase.RequestLocationUpdates
import de.thb.ui.util.hasLocationPermission
import de.thb.ui.util.rememberMapViewWithLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get

data class RouteState(
    val count: Int = 0,
    val location: Location? = null,
) : MavericksState

class RouteViewModel(
    initialState: RouteState,
) : MavericksViewModel<RouteState>(initialState) {

    fun requestLocationUpdates(useCase: RequestLocationUpdates) {
        val locationRepository = LocationDataSourceImpl.getInstance(useCase.context)

        viewModelScope.launch {
            locationRepository
                .requestLocationUpdates(LocationRequest.create())
                .sample(periodMillis = 1000)
                .collect { setState { copy(location = it) } }
        }
    }
}

@Composable
fun RouteScreen(viewModel: RouteViewModel = mavericksViewModel()) {
    val context = LocalContext.current

    val requestLocationPermissionLauncher =
        rememberLauncherForActivityResult(RequestPermission()) { isGranted ->
            if (isGranted) {
                viewModel.requestLocationUpdates(RequestLocationUpdates(context))
            }
        }

    if (hasLocationPermission(context)) {
        viewModel.requestLocationUpdates(RequestLocationUpdates(context))
    } else {
        SideEffect {
            requestLocationPermissionLauncher.launch(ACCESS_FINE_LOCATION)
        }
    }

    // val deviceLocation by viewModel.collectAsState(RouteState::location)
    // PlacesScreenContent(deviceLocation = deviceLocation, context = context)
}

@Composable
private fun PlacesScreenContent(
    context: Context,
    deviceLocation: Location?,
    geoApiContext: GeoApiContext = get(),
) {
    val mapView = rememberMapViewWithLifecycle()

    MapView(
        map = mapView,
        context = context,
        deviceLocation = deviceLocation,
        geoApiContext = geoApiContext
    )
}
