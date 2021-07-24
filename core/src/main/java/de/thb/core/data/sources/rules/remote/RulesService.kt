package de.thb.core.data.sources.rules.remote

import de.thb.core.domain.rule.RuleReponse
import retrofit2.http.GET
import retrofit2.http.Path

interface RulesService {

    @GET("/places/{id}/rules")
    suspend fun getRulesByPlaceId(
        @Path("id") placeId: String
    ): List<RuleReponse>
}
