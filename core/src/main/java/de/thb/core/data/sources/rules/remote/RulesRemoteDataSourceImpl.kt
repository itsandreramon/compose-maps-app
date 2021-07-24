package de.thb.core.data.sources.rules.remote

import de.thb.core.domain.rule.RuleReponse
import de.thb.core.util.CoroutinesDispatcherProvider
import kotlinx.coroutines.withContext

class RulesRemoteDataSourceImpl(
    private val rulesService: RulesService,
    private val dispatcherProvider: CoroutinesDispatcherProvider,
) : RulesRemoteDataSource {

    override suspend fun getRulesByPlaceId(placeId: String): List<RuleReponse> {
        return withContext(dispatcherProvider.io()) {
            rulesService.getRulesByPlaceId(placeId)
        }
    }
}
