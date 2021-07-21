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
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import de.thb.core.util.LatLng
import de.thb.core.util.MapLatLng
import de.thb.core.util.Result
import de.thb.ui.R
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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

fun Location.toMapLatLng(): MapLatLng {
    return MapLatLng(latitude, longitude)
}

fun Location.toLatLng(): LatLng {
    return LatLng(latitude, longitude)
}

suspend fun calculateDirections(
    geoApiContext: GeoApiContext,
    destination: LatLng,
    origin: LatLng,
): Result<DirectionsResult> {
    return suspendCancellableCoroutine { cont ->
        DirectionsApiRequest(geoApiContext).apply {
            origin(origin)
            destination(destination)
        }.setCallback(object : PendingResult.Callback<DirectionsResult> {
            override fun onResult(result: DirectionsResult) {
                cont.resume(Result.Success(result))
            }

            override fun onFailure(e: Throwable) {
                cont.resume(Result.Error(e))
            }
        })
    }
}
