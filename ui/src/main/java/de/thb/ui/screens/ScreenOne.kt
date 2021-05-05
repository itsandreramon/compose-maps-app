package de.thb.ui.screens

import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
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
import de.thb.ui.util.rememberMapViewWithLifecycle
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

data class ScreenOneState(
    val count: Int = 0,
    val location: Location? = null
) : MavericksState

class ScreenOneViewModel(
    initialState: ScreenOneState,
) : MavericksViewModel<ScreenOneState>(initialState) {

    fun observeLocationChanges(context: Context) {
        val locationRepository = LocationRepositoryImpl.getInstance(context)

        viewModelScope.launch {
            locationRepository.requestLocationUpdates(LocationRequest.create()).collect {
                setState { copy(location = it) }
            }
        }
    }

    fun increment() = setState {
        copy(count = count + 1)
    }
}

@Composable
fun ScreenOne() {
    val mapView = rememberMapViewWithLifecycle()
    val viewModel: ScreenOneViewModel = mavericksViewModel()

    val count by viewModel.collectAsState(ScreenOneState::count)
    val location by viewModel.collectAsState(ScreenOneState::location)

    viewModel.observeLocationChanges(LocalContext.current)

    Log.e("Location", "$location")

    Column(
        modifier = Modifier
            .padding(16.dp)
            .statusBarsPadding()
    ) {
        ScreenTitle(title = "One")

        Button(onClick = viewModel::increment) {
            Text("Click")
        }

        Spacer(Modifier.padding(vertical = 16.dp))

        Text("Clicked $count times")

        MapView(map = mapView)
    }
}
