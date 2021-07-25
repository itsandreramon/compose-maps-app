package de.thb.core.manager

import android.location.Geocoder
import com.google.maps.DirectionsApiRequest
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.PendingResult
import com.google.maps.model.DirectionsResult
import com.google.maps.model.EncodedPolyline
import com.google.maps.model.TravelMode
import de.thb.core.util.CoroutinesDispatcherProvider
import de.thb.core.util.LatLng
import de.thb.core.util.MapLatLng
import de.thb.core.util.Result
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

interface RouteManager {

    suspend fun getLatLngByName(
        name: String
    ): MapLatLng?

    suspend fun getDirections(
        startLatLng: LatLng,
        endLatLng: LatLng,
    ): Result<DirectionsResult>

    suspend fun getDirectionsPolyline(
        result: DirectionsResult
    ): EncodedPolyline?

    suspend fun getDirectionsPolyline(
        startLatLng: LatLng,
        endLatLng: LatLng,
    ): EncodedPolyline?
}

class RouteManagerImpl(
    private val dispatcherProvider: CoroutinesDispatcherProvider,
    private val geoApiContext: GeoApiContext,
) : RouteManager {

    override suspend fun getLatLngByName(name: String): MapLatLng? {
        return withContext(dispatcherProvider.io()) {
            runCatching {
                val result = GeocodingApi
                    .geocode(geoApiContext, name)
                    .await()
                    .getOrNull(0)

                result?.let {
                    MapLatLng(
                        it.geometry.location.lat,
                        it.geometry.location.lng
                    )
                }
            }.getOrNull()
        }
    }

    override suspend fun getDirectionsPolyline(
        startLatLng: LatLng,
        endLatLng: LatLng
    ): EncodedPolyline? {
        return when (val result = getDirections(startLatLng, endLatLng)) {
            is Result.Success -> getDirectionsPolyline(result.data)
            is Result.Error -> null
        }
    }

    override suspend fun getDirectionsPolyline(
        result: DirectionsResult
    ): EncodedPolyline? {
        val route = if (result.routes.isNotEmpty()) {
            result.routes[0]
        } else {
            return null
        }

        return route.overviewPolyline
    }

    override suspend fun getDirections(
        startLatLng: LatLng,
        endLatLng: LatLng
    ): Result<DirectionsResult> {
        val request = DirectionsApiRequest(geoApiContext)
            .origin(startLatLng)
            .destination(endLatLng)
            .mode(TravelMode.DRIVING)

        return suspendCancellableCoroutine { cont ->
            request.setCallback(object : PendingResult.Callback<DirectionsResult> {
                override fun onResult(result: DirectionsResult) {
                    cont.resume(Result.Success(result))
                }

                override fun onFailure(e: Throwable) {
                    cont.resume(Result.Error(e))
                }
            })
        }
    }
}
