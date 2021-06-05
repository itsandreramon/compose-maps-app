package de.thb.core.data.filters.local

import de.thb.core.domain.FilterEntity
import kotlinx.coroutines.flow.Flow

interface FiltersLocalDataSource {

    suspend fun insert(filters: List<FilterEntity>)

    suspend fun insert(filter: FilterEntity)

    fun getAll(): Flow<List<FilterEntity>>
}