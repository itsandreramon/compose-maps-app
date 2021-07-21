package de.thb.core.data.categories.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thb.core.domain.category.CategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoriesRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(categories: List<CategoryEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(category: CategoryEntity)

    @Query("SELECT * FROM categories")
    fun getAll(): Flow<List<CategoryEntity>>
}
