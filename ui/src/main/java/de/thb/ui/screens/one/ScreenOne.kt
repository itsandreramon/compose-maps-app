package de.thb.ui.screens.one

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
import de.thb.core.data.LocationRepositoryImpl
import de.thb.ui.components.MapView
import de.thb.ui.components.ScreenTitle
import de.thb.ui.screens.one.ScreenOneUseCase.RequestLocationUpdates
import de.thb.ui.util.hasLocationPermission
import de.thb.ui.util.rememberMapViewWithLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.launch

data class ScreenOneState(
    val count: Int = 0,
    val location: Location? = null,
) : MavericksState

class ScreenOneViewModel(
    initialState: ScreenOneState,
) : MavericksViewModel<ScreenOneState>(initialState) {

    fun requestLocationUpdates(useCase: RequestLocationUpdates) {
        val locationRepository = LocationRepositoryImpl.getInstance(useCase.context)

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
fun ScreenOne() {
    val viewModel: ScreenOneViewModel = mavericksViewModel()
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

    val count by viewModel.collectAsState(ScreenOneState::count)
    val deviceLocation by viewModel.collectAsState(ScreenOneState::location)

    Log.e("Location", "$deviceLocation")

    ScreenOneContent(
        onButtonClick = viewModel::increment,
        count = count,
        deviceLocation = deviceLocation,
    )
}

@Composable
private fun ScreenOneContent(
    onButtonClick: () -> Unit,
    count: Int,
    deviceLocation: Location?,
) {
    val mapView = rememberMapViewWithLifecycle()

    Column(
        modifier = Modifier
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        ScreenTitle(title = "One")

        Button(onClick = onButtonClick) {
            Text("Click")
        }

        Spacer(Modifier.padding(vertical = 16.dp))

        Text("Clicked $count times")

        MapView(mapView, LocalContext.current, deviceLocation)
    }
}
