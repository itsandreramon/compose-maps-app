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
    suspend fun insert(places: List<PlaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceEntity)

    @Query("SELECT * FROM places")
    fun getAll(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places WHERE uuid = :uuid LIMIT 1")
    fun getByUuid(uuid: String): Flow<PlaceEntity>
}
