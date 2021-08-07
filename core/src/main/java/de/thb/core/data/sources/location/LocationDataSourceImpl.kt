package de.thb.core.data.sources.location

import android.Manifest
import android.content.Context
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import de.thb.core.util.MapLatLng
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class LocationDataSourceImpl private constructor(
    private val locationClient: FusedLocationProviderClient
) : LocationDataSource {

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ]
    )
    override fun getLastLocation() = flow {
        val location = locationClient.lastLocation.await()
        emit(MapLatLng(location.latitude, location.longitude))
    }

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ]
    )
    override fun requestLocationUpdates(
        request: LocationRequest
    ): Flow<MapLatLng> = locationClient.locationFlow(request)

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ]
    )
    fun FusedLocationProviderClient.locationFlow(
        request: LocationRequest,
    ) = callbackFlow {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                result ?: return
                val location = result.locations.first()
                Log.e("Location", "got result: $location")
                trySend(MapLatLng(location.latitude, location.longitude))
            }
        }

        requestLocationUpdates(
            request,
            callback,
            Looper.getMainLooper()
        ).addOnFailureListener { e ->
            close(e)
        }

        awaitClose {
            removeLocationUpdates(callback) // clean up when Flow collection ends
        }
    }

    companion object {

        private var instance: LocationDataSource? = null

        fun getInstance(context: Context): LocationDataSource {
            return instance ?: LocationDataSourceImpl(
                LocationServices.getFusedLocationProviderClient(context)
            ).also { instance = it }
        }
    }
}
