package de.thb.core.util

import android.util.Log
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.category.CategoryResponse
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.place.PlaceResponse
import de.thb.core.domain.rule.RuleEntity
import de.thb.core.domain.rule.RuleReponse
import de.thb.core.domain.rule.RuleWithCategoryEntity

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
            incidence = incidence ?: 0.0,
            website = website ?: "",
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
            trend = trend,
            incidence = incidence ?: placeEntity.incidence,
            website = website ?: placeEntity.website,
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

object RuleUtils {

    fun groupRulesByCategory(
        rules: List<RuleWithCategoryEntity>
    ): List<Pair<CategoryEntity, List<RuleEntity>>> {
        return rules
            .groupBy { it.category }
            .map { entry ->
                val category = entry.key
                val rulesForCategory = entry.value.map { it.rule }
                Pair(category, rulesForCategory)
            }
    }

    /**
     * Converts the response into an entity object,
     * but maps it to a given place.
     *
     * @param placeId
     */
    fun RuleReponse.toEntity(placeId: String): RuleEntity {
        return RuleEntity(
            id = id,
            categoryId = categoryId,
            placeId = placeId,
            status = status,
            restriction = restriction,
            text = text,
            timestamp = timestamp,
        )
    }

    /**
     * Converts a list of responses into a list of entities.
     */
    fun List<RuleReponse>.toEntities(placeId: String): List<RuleEntity> {
        return map { it.toEntity(placeId) }
    }
}

/**
 * Genereic helper function that allows to split a response
 * into two parts:
 *
 * 1. The data that already exists locally but needs to be
 *    updated with local data.
 * 2. The data that not already exists and is safe to be
 *    inserted locally.
 *
 * DISCLAIMER: It is currently not parallelized but should be done
 * in the future to ensure the best performance.
 *
 * @param responseData The fetched response data
 * @param localData The already existing local data
 * @param predicate The predicate to split the data
 * @param updater The function to update local data with fetched data
 * @param mapper The function to map fetched data to local data
 * @param onUpdateRequested Callback that is being called with updated data
 * @param onInsertRequested Callback that is being called with mapped data
 */
suspend fun <R, T> responseToEntityIfExistsElseResponse(
    responseData: List<R>,
    localData: List<T>,
    predicate: (R, T) -> Boolean,
    updater: (R, T) -> T,
    mapper: (R) -> T,
    onUpdateRequested: suspend (List<T>) -> Unit,
    onInsertRequested: suspend (List<T>) -> Unit,
) {
    Log.e("RESPONSE", "responseToEntityIfExistsElseResponse")
    Log.e("RESPONSE LOCAL", "$localData")
    Log.e("RESPONSE RESPONSE", "$responseData")

    val (toUpdate, toInsert) = responseData.partition { response ->
        localData.any { entity -> predicate(response, entity) }
    }

    Log.e("UPDATING", "$toUpdate")
    Log.e("INSERTING", "$toInsert")

    toUpdate.mapNotNull { response ->
        val localDataMaybe = localData.firstOrNull { entity -> predicate(response, entity) }

        localDataMaybe?.let { entity ->
            updater(response, entity)
        }
    }.let { updatedPlaces ->
        if (updatedPlaces.isNotEmpty()) {
            onUpdateRequested(updatedPlaces)
        }
    }

    onInsertRequested(toInsert.map(mapper))
}
