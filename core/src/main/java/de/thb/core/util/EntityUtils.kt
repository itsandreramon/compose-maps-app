package de.thb.core.util

import de.thb.core.domain.PlaceEntity
import de.thb.core.domain.PlaceResponse

fun PlaceResponse.toEntity(): PlaceEntity {
    return PlaceEntity(uuid, name)
}

fun List<PlaceResponse>.toEntities(): List<PlaceEntity> {
    return map { it.toEntity() }
}
