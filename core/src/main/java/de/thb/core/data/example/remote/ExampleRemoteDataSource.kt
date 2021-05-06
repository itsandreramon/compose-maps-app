package de.thb.core.data.example.remote

import de.thb.core.domain.ExampleEntity

interface ExampleRemoteDataSource {
    suspend fun getAll(): List<ExampleEntity>
}
