package de.thb.ui.screens.route

import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.location.Location
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.airbnb.mvrx.MavericksState
import com.airbnb.mvrx.MavericksViewModel
import com.airbnb.mvrx.compose.collectAsState
import com.airbnb.mvrx.compose.mavericksViewModel
import com.google.accompanist.insets.statusBarsPadding
import com.google.android.gms.location.LocationRequest
import com.google.maps.GeoApiContext
import de.thb.core.data.location.LocationDataSourceImpl
import de.thb.ui.components.MapView
import de.thb.ui.components.ScreenTitle
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

    fun increment() = setState {
        copy(count = count + 1)
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

    val count by viewModel.collectAsState(RouteState::count)
    val deviceLocation by viewModel.collectAsState(RouteState::location)

    Log.e("Location", "$deviceLocation")

    PlacesScreenContent(
        onButtonClick = viewModel::increment,
        count = count,
        deviceLocation = deviceLocation,
    )
}

@Composable
private fun PlacesScreenContent(
    onButtonClick: () -> Unit,
    count: Int,
    deviceLocation: Location?,
    geoApiContext: GeoApiContext = get(),
) {
    val mapView = rememberMapViewWithLifecycle()

    Column(Modifier.statusBarsPadding()) {
        Column(Modifier.padding(16.dp)) {
            ScreenTitle(title = "Route")

            Button(onClick = onButtonClick) {
                Text("Click")
            }

            Spacer(Modifier.padding(vertical = 16.dp))

            Text("Clicked $count times")
        }

        MapView(mapView, LocalContext.current, deviceLocation, geoApiContext)
    }
}
