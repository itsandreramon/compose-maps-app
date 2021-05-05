package de.thb.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.coroutineScope
import com.google.android.gms.maps.MapView
import com.google.maps.android.ktx.awaitMap

@Composable
fun MapView(map: MapView) {
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    val scope = remember { lifecycle.coroutineScope }

    AndroidView({ map }) { mapView ->
        scope.launchWhenCreated {
            mapView.awaitMap()
        }
    }
}
