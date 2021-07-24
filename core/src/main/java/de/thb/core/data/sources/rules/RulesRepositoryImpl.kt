package de.thb.core.data.sources.rules

import com.dropbox.android.external.store4.Fetcher
import com.dropbox.android.external.store4.SourceOfTruth
import com.dropbox.android.external.store4.StoreBuilder
import com.dropbox.android.external.store4.StoreRequest
import com.dropbox.android.external.store4.StoreResponse
import de.thb.core.data.sources.rules.local.RulesLocalDataSource
import de.thb.core.data.sources.rules.remote.RulesRemoteDataSource
import de.thb.core.domain.rule.RuleEntity
import de.thb.core.domain.rule.RuleReponse
import de.thb.core.domain.rule.RuleWithCategoryEntity
import de.thb.core.util.RuleUtils.toEntities
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow

class RulesRepositoryImpl(
    private val rulesLocalDataSource: RulesLocalDataSource,
    private val rulesRemoteDataSource: RulesRemoteDataSource,
) : RulesRepository {

    private val getByIdStore = StoreBuilder.from(
        fetcher = Fetcher.of { id: String ->
            Pair(id, rulesRemoteDataSource.getRulesByPlaceId(id))
        },
        sourceOfTruth = SourceOfTruth.Companion.of(
            reader = { id: String -> rulesLocalDataSource.getByPlaceId(id) },
            writer = { _, input -> insert(input.second, input.first) }
        )
    ).build()

    override fun getByPlaceId(placeId: String) = flow<List<RuleWithCategoryEntity>> {
        getByIdStore.stream(StoreRequest.cached(placeId, refresh = true)).collect { response ->
            when (response) {
                is StoreResponse.Data -> emit(response.value)
                else -> emit(emptyList())
            }
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
