package de.thb.core.data.sources.rules.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import de.thb.core.domain.rule.RuleEntity
import de.thb.core.domain.rule.RuleWithCategoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface RulesRoomDao {

    @Transaction
    @Query("SELECT * FROM rules WHERE place_id = :placeId")
    fun getByPlaceId(placeId: String): Flow<List<RuleWithCategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rule: RuleEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(rules: List<RuleEntity>)
}
