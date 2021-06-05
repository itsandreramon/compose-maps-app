package de.thb.core.data.filters.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thb.core.domain.FilterEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FiltersRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(filters: List<FilterEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(filter: FilterEntity)

    @Query("SELECT * FROM filters")
    fun getAll(): Flow<List<FilterEntity>>
}