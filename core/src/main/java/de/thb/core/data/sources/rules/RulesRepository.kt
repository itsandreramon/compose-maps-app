package de.thb.core.data.sources.rules

import de.thb.core.domain.rule.RuleEntity
import de.thb.core.domain.rule.RuleWithCategoryEntity
import kotlinx.coroutines.flow.Flow

interface RulesRepository {

    fun getByPlaceId(placeId: String): Flow<List<RuleWithCategoryEntity>>

    suspend fun insert(rule: RuleEntity)
}
