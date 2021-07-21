package de.thb.core.util

import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.category.CategoryResponse
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse

object CategoryUtils {

    /**
     * Converts the response into an entity object.
     */
    fun CategoryResponse.toEntity(): CategoryEntity {
        return CategoryEntity(id, name)
    }

    /**
     * Converts the response into an entity object,
     * but keeps existing local data.
     *
     * @param categoryEntity
     */
    fun CategoryResponse.toEntity(categoryEntity: CategoryEntity): CategoryEntity {
        return CategoryEntity(
            id = id,
            name = name,
            added = categoryEntity.added,
        )
    }

    /**
     * Converts a list of responses into a list of entities.
     */
    fun List<CategoryResponse>.toEntities(): List<CategoryEntity> {
        return map { it.toEntity() }
    }
}

object PlaceUtils {

    /**
     * Converts the response into an entity object.
     */
    fun PlaceResponse.toEntity(): PlaceEntity {
        return PlaceEntity(
            id = id,
            name = name,
            type = type,
            trend = trend,
            incidence = incidence,
            website = website,
            example = example,
        )
    }

    /**
     * Converts the response into an entity object,
     * but keeps existing local data.
     *
     * @param placeEntity
     */
    fun PlaceResponse.toEntity(placeEntity: PlaceEntity): PlaceEntity {
        return PlaceEntity(
            id = id,
            name = name,
            type = type,
            incidence = incidence,
            website = website,
            example = example,
            searchedAtUtc = placeEntity.searchedAtUtc,
            isBookmarked = placeEntity.isBookmarked,
        )
    }

    /**
     * Converts a list of responses into a list of entities.
     */
    fun List<PlaceResponse>.toEntities(): List<PlaceEntity> {
        return map { it.toEntity() }
    }
}
