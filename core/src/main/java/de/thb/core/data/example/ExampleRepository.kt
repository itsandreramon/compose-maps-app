package de.thb.core.data.example

import de.thb.core.domain.ExampleEntity
import kotlinx.coroutines.flow.Flow

interface ExampleRepository {
    fun getAll(): Flow<List<ExampleEntity>>
}
