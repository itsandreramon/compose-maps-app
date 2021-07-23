package de.thb.core.data.sources.categories.local

import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.category.CategoryResponse
import de.thb.core.util.CategoryUtils.toEntity
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class CategoriesLocalDataSourceImpl(
    private val applicationScope: CoroutineScope,
    private val categoriesRoomDao: CategoriesRoomDao,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : CategoriesLocalDataSource {

    override suspend fun insertOrUpdate(categoryResponse: CategoryResponse) {
        val existingCategoryMaybe = getByIdOnce(categoryResponse.id)

        if (existingCategoryMaybe != null) {
            if (existingCategoryMaybe != categoryResponse.toEntity()) {
                insert(categoryResponse.toEntity(existingCategoryMaybe))
            }
        } else {
            insert(categoryResponse.toEntity())
        }
    }

    override suspend fun insert(categories: List<CategoryEntity>) {
        withContext(dispatcherProvider.database()) {
            applicationScope.launch {
                categoriesRoomDao.insert(categories)
            }.join()
        }
    }

    override suspend fun insert(category: CategoryEntity) {
        withContext(dispatcherProvider.database()) {
            applicationScope.launch {
                categoriesRoomDao.insert(category)
            }.join()
        }
    }

    override suspend fun getByIdOnce(id: Long): CategoryEntity? {
        return withContext(dispatcherProvider.database()) {
            categoriesRoomDao.getByIdOnce(id)
        }
    }

    override suspend fun getAllOnce(): List<CategoryEntity> {
        return withContext(dispatcherProvider.database()) {
            categoriesRoomDao.getAllOnce()
        }
    }

    override fun getAll(): Flow<List<CategoryEntity>> {
        return categoriesRoomDao.getAll()
            .flowOn(dispatcherProvider.database())
    }
}
