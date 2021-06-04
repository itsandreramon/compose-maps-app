package de.thb.core.data.places.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import de.thb.core.domain.PlaceEntity
import kotlinx.coroutines.flow.Flow

interface PlacesLocalDataSource {

    suspend fun insert(places: List<PlaceEntity>)

    suspend fun insert(place: PlaceEntity)

    fun getAll(): Flow<List<PlaceEntity>>

    fun getByUuid(uuid: String): Flow<PlaceEntity>
}
