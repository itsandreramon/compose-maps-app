package de.thb.core.data.places.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thb.core.domain.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesLocalDataSource {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(examples: List<PlaceEntity>)

    @Query("SELECT * FROM places")
    fun getAll(): Flow<List<PlaceEntity>>
}