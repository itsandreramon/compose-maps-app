package de.thb.core.data.filters.local

import de.thb.core.domain.FilterEntity
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class FiltersLocalDataSourceImpl(
    private val filtersRoomDao: FiltersRoomDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : FiltersLocalDataSource {

    override suspend fun insert(filters: List<FilterEntity>) {
        withContext(dispatcherProvider.database()) {
            filtersRoomDao.insert(filters)
        }
    }

    override suspend fun insert(filter: FilterEntity) {
        withContext(dispatcherProvider.database()) {
            filtersRoomDao.insert(filter)
        }
    }

    override fun getAll(): Flow<List<FilterEntity>> {
        return filtersRoomDao.getAll()
            .flowOn(dispatcherProvider.database())
    }
}
