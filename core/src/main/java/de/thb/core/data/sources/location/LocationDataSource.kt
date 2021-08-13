package de.thb.core.data.sources.location

import com.google.android.gms.location.LocationRequest
import de.thb.core.util.MapLatLng
import de.thb.core.util.Result
import kotlinx.coroutines.flow.Flow

interface LocationDataSource {
    fun getLastLocation(): Flow<Result<MapLatLng>>
    fun requestLocationUpdates(request: LocationRequest): Flow<MapLatLng>
}
