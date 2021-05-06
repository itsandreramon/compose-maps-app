package de.thb.core.data.example.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import de.thb.core.domain.ExampleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ExampleLocalDataSource {

    @Insert
    suspend fun insert(examples: List<ExampleEntity>)

    @Query("SELECT * FROM examples")
    fun getAll(): Flow<List<ExampleEntity>>
}
