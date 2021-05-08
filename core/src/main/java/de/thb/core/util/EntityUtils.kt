package de.thb.core.util

import de.thb.core.domain.ExampleEntity
import de.thb.core.domain.ExampleResponse

fun ExampleResponse.toEntity(): ExampleEntity {
    return ExampleEntity(id, name)
}

fun List<ExampleResponse>.toEntities(): List<ExampleEntity> {
    return map { it.toEntity() }
}
