package de.thb.core.manager

import android.util.Log
import de.thb.core.data.sources.district.DistrictRemoteDataSource
import de.thb.core.data.sources.places.PlacesRepository
import de.thb.core.util.LatLng
import de.thb.core.util.MapLatLng
import kotlinx.coroutines.flow.first

interface RouteManager {
    suspend fun getPlaceIdByLatLng(currLocation: MapLatLng): String?
}

class RouteManagerImpl(
    private val districtRemoteDataSource: DistrictRemoteDataSource,
    private val placesRepository: PlacesRepository,
) : RouteManager {

    override suspend fun getPlaceIdByLatLng(currLocation: MapLatLng): String? {
        val district = districtRemoteDataSource.getByLatLng(
            LatLng(
                currLocation.latitude,
                currLocation.longitude
            )
        )

        Log.e("RouteManager", "found district: $district")

        return placesRepository.getAll().first().firstOrNull {
            it.name == district
        }?.id
    }
}
