package de.thb.core.data.sources.rules.remote

import de.thb.core.domain.rule.RuleReponse

interface RulesRemoteDataSource {
    suspend fun getRulesByPlaceId(placeId: String): List<RuleReponse>
}
