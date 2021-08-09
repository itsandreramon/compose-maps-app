package de.thb.core.domain.route.type

import de.thb.core.domain.Geojson
import de.thb.core.domain.rule.RuleReponse

data class RestrictedPlace(
    val placeId: String,
    val denyingRules: List<RuleReponse>,
    val polygon: Geojson,
)