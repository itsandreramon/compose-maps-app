package de.thb.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.location.Location
import android.util.Log
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.coroutineScope
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.model.CameraPosition
import com.google.maps.GeoApiContext
import com.google.maps.android.ktx.awaitMap
import de.thb.ui.util.GmsLatLng
import de.thb.ui.util.calculateDirections
import de.thb.ui.util.hasLocationPermission
import de.thb.ui.util.toGmsLatLng
import de.thb.ui.util.toMapLatLng

@SuppressLint("MissingPermission")
@Composable
fun MapView(
    map: MapView,
    context: Context,
    deviceLocation: Location?,
    geoApiContext: GeoApiContext
) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = remember { lifecycle.coroutineScope }
    val isShownFirstTime = remember { mutableStateOf(true) }

    AndroidView(
        factory = { map },
        modifier = Modifier.fillMaxSize()
    ) { mapView ->
        scope.launchWhenCreated {
            with(mapView.awaitMap()) {
                if (hasLocationPermission(context)) {
                    isMyLocationEnabled = true
                    uiSettings.isMyLocationButtonEnabled = false

                    if (deviceLocation != null && isShownFirstTime.value) {
                        centerOnLocation(deviceLocation)
                        isShownFirstTime.value = false

                        calculateDirections(
                            geoApiContext = geoApiContext,
                            destination = GmsLatLng(52.520008, 13.404954),
                            origin = deviceLocation.toGmsLatLng()
                        ).also { Log.d("Map", "$it") }
                    }
                }
            }
        }
    }
}

private fun GoogleMap.centerOnLocation(location: Location) {
    animateCamera(
        CameraUpdateFactory.newCameraPosition(
            CameraPosition.fromLatLngZoom(
                location.toMapLatLng(), 10f
            )
        )
    )
}
