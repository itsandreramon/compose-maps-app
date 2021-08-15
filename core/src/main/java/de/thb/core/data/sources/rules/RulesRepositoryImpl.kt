package de.thb.core.data.sources.rules

import de.thb.core.data.sources.rules.local.RulesLocalDataSource
import de.thb.core.data.sources.rules.remote.RulesRemoteDataSource
import de.thb.core.domain.rule.RuleEntity
import de.thb.core.domain.rule.RuleReponse
import de.thb.core.util.RuleUtils.toEntities
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import okio.IOException

class RulesRepositoryImpl(
    private val rulesLocalDataSource: RulesLocalDataSource,
    private val rulesRemoteDataSource: RulesRemoteDataSource,
) : RulesRepository {

    override fun getByPlaceId(placeId: String) = flow {
        val rules = try {
            rulesRemoteDataSource.getRulesByPlaceId(placeId)
        } catch (e: IOException) {
            emptyList()
        }

        insert(rules, placeId)

        rulesLocalDataSource.getByPlaceId(placeId).collect {
            emit(it)
        }
    }

    override suspend fun insert(rule: RuleEntity) {
        rulesLocalDataSource.insert(rule)
    }

    private suspend fun insert(ruleReponses: List<RuleReponse>, placeId: String) {
        val ruleEntities = ruleReponses.toEntities(placeId)
        rulesLocalDataSource.insert(ruleEntities)
    }
}
