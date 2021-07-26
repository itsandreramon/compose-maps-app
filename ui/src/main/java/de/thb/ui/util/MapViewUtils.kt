package de.thb.ui.util

import android.location.Location
import android.os.Bundle
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.gms.maps.MapView
import com.google.maps.model.EncodedPolyline
import de.thb.core.util.LatLng
import de.thb.core.util.MapLatLng
import de.thb.ui.R

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle

    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}

@Composable
private fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver {
    return remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }
}

fun LatLng.toMapLatLng(): MapLatLng {
    return MapLatLng(lat, lng)
}

fun MapLatLng.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun Location.toMapLatLng(): MapLatLng {
    return MapLatLng(latitude, longitude)
}

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

fun decodePolylineForMapView(encodedPolyline: EncodedPolyline): List<MapLatLng> {
    return encodedPolyline
        .decodePath()
        .map { it.toMapLatLng() }
}

fun encodePolylineByCoordinates(coordinates: List<LatLng>): EncodedPolyline {
    return EncodedPolyline(coordinates)
}
