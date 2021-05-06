package de.thb.core.data.location

import android.Manifest
import android.content.Context
import android.location.Location
import android.os.Looper
import androidx.annotation.RequiresPermission
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class LocationRepositoryImpl private constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) : LocationRepository {

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ]
    )
    override fun getLastLocation() = flow {
        emit(fusedLocationClient.lastLocation.await())
    }

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ]
    )
    override fun requestLocationUpdates(
        request: LocationRequest
    ): Flow<Location> = fusedLocationClient.locationFlow(request)

    @RequiresPermission(
        anyOf = [
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION
        ]
    )
    private fun FusedLocationProviderClient.locationFlow(
        request: LocationRequest
    ) = callbackFlow<Location> {
        val callback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult?) {
                result ?: return
                for (location in result.locations) {
                    offer(location)
                }
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
            removeLocationUpdates(callback)
        }
    }

    companion object {

        fun getInstance(context: Context): LocationRepository {
            return LocationRepositoryImpl(
                LocationServices.getFusedLocationProviderClient(context)
            )
        }
    }
}
