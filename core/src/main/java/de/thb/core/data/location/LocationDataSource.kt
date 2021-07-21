package de.thb.core.data.location

import com.google.android.gms.location.LocationRequest
import de.thb.core.util.MapLatLng
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    fun getLastLocation(): Flow<MapLatLng>
    fun requestLocationUpdates(request: LocationRequest): Flow<MapLatLng>
}
