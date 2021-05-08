package de.thb.core.data.example.remote

import de.thb.core.domain.ExampleResponse

class ExampleRemoteDataSourceImpl(
    private val exampleService: ExampleService
) : ExampleRemoteDataSource {

    override suspend fun getAll(): List<ExampleResponse> {
        return exampleService.getAll()
    }
}
