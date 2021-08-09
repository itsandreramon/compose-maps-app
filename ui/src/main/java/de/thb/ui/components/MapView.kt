package de.thb.ui.components

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
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
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import de.thb.core.util.MapLatLng
import de.thb.ui.util.hasLocationPermission
import de.thb.ui.util.pxFromDp

@SuppressLint("MissingPermission")
@Composable
fun MapView(
    map: MapView,
    context: Context,
    location: MapLatLng?,
    polyline: List<MapLatLng> = listOf(),
    boundaries: List<List<MapLatLng>> = listOf()
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
                    uiSettings.isZoomControlsEnabled = true

                    if (location != null && isShownFirstTime.value) {
                        centerOnLocation(location)
                        isShownFirstTime.value = false
                    }

                    if (polyline.isNotEmpty()) {
                        val destination = polyline.lastOrNull()

                        addPolyline(
                            PolylineOptions()
                                .addAll(polyline)
                                .color(Color.parseColor("#659EF6"))
                        )

                        destination?.let { latLng ->
                            addMarker(MarkerOptions().position(latLng))
                        }

                        with(LatLngBounds.Builder()) {
                            include(polyline.first())
                            include(polyline.last())

                            val padding = pxFromDp(context, 64f)

                            CameraUpdateFactory
                                .newLatLngBounds(build(), padding)
                                .also { animateCamera(it) }
                        }
                    }

                    for (boundary in boundaries) {
                        addPolyline(
                            PolylineOptions()
                                .addAll(boundary)
                                .color(Color.RED)
                        )
                    }
                }
            }
        }
    }
}

fun GoogleMap.centerOnLocation(latLng: MapLatLng) {
    moveCamera(
        CameraUpdateFactory.newCameraPosition(
            CameraPosition.fromLatLngZoom(latLng, 10f)
        )
    )
}
