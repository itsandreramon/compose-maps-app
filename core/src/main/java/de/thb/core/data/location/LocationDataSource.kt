package de.thb.core.data.location

import android.location.Location
import com.google.android.gms.location.LocationRequest
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    fun getLastLocation(): Flow<Location>
    fun requestLocationUpdates(request: LocationRequest): Flow<Location>
}
