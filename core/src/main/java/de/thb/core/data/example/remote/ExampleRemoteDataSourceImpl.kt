package de.thb.core.data.example.remote

import de.thb.core.domain.ExampleEntity
import javax.inject.Inject

class ExampleRemoteDataSourceImpl @Inject constructor(
    private val exampleService: ExampleService
) : ExampleRemoteDataSource {

    override suspend fun getAll(): List<ExampleEntity> {
        return exampleService.getAll()
    }
}
