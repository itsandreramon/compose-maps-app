package de.thb.core.data.example.remote

import de.thb.core.domain.ExampleResponse

interface ExampleRemoteDataSource {
    suspend fun getAll(): List<ExampleResponse>
}
