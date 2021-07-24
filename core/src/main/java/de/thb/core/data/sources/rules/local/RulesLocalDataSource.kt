package de.thb.core.data.sources.rules.local

import de.thb.core.domain.rule.RuleEntity
import de.thb.core.domain.rule.RuleWithCategoryEntity
import kotlinx.coroutines.flow.Flow

interface RulesLocalDataSource {

    fun getByPlaceId(placeId: String): Flow<List<RuleWithCategoryEntity>>

    suspend fun insert(rule: RuleEntity)

    suspend fun insert(rules: List<RuleEntity>)
}
