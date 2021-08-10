package de.thb.core.domain.rule

import android.os.Parcelable
import androidx.room.Embedded
import androidx.room.Relation
import de.thb.core.domain.category.CategoryEntity
import kotlinx.parcelize.Parcelize

@Parcelize
data class RuleWithCategoryEntity(

    @Embedded
    val rule: RuleEntity,

    @Relation(
        parentColumn = "category_id",
        entityColumn = "id",
        entity = CategoryEntity::class
    )
    val category: CategoryEntity?
) : Parcelable
