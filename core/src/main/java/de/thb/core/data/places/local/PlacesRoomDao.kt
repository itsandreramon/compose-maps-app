package de.thb.core.data.places.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thb.core.domain.place.PlaceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlacesRoomDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(places: List<PlaceEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(place: PlaceEntity)

    @Query("SELECT * FROM places WHERE id = :id")
    suspend fun getByIdOnce(id: String): PlaceEntity?

    @Query("SELECT * FROM places")
    fun getAll(): Flow<List<PlaceEntity>>

    @Query("SELECT * FROM places")
    fun getAllOnce(): List<PlaceEntity>

    @Query("SELECT * FROM places WHERE id = :id LIMIT 1")
    fun getById(id: String): Flow<PlaceEntity>
}
