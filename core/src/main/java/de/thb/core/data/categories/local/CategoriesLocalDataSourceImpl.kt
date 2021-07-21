package de.thb.core.data.categories.local

import de.thb.core.domain.category.CategoryEntity
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext

class CategoriesLocalDataSourceImpl(
    private val categoriesRoomDao: CategoriesRoomDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : CategoriesLocalDataSource {

    override suspend fun insert(categories: List<CategoryEntity>) {
        withContext(dispatcherProvider.database()) {
            categoriesRoomDao.insert(categories)
        }
    }

    override suspend fun insert(category: CategoryEntity) {
        withContext(dispatcherProvider.database()) {
            categoriesRoomDao.insert(category)
        }
    }

    override fun getAll(): Flow<List<CategoryEntity>> {
        return categoriesRoomDao.getAll()
            .flowOn(dispatcherProvider.database())
    }
}
