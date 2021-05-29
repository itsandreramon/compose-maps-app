package de.thb.core.data.places.remote

import de.thb.core.domain.PlaceResponse

class PlacesRemoteDataSourceImpl(
    private val exampleService: PlacesService
) : PlacesRemoteDataSource {

    override suspend fun getAll(): List<PlaceResponse> {
        return exampleService.getAll()
    }
}
