package de.thb.core.data.example.remote

import de.thb.core.domain.ExampleEntity

class ExampleRemoteDataSourceImpl(
    private val exampleService: ExampleService
) : ExampleRemoteDataSource {

    override suspend fun getAll(): List<ExampleEntity> {
        return exampleService.getAll()
    }
}
