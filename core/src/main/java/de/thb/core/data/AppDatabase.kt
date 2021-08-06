package de.thb.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import de.thb.core.data.sources.categories.local.CategoriesRoomDao
import de.thb.core.data.sources.places.local.PlacesRoomDao
import de.thb.core.data.sources.rules.local.RulesRoomDao
import de.thb.core.domain.category.CategoryEntity
import de.thb.core.domain.place.PlaceEntity
import de.thb.core.domain.rule.RuleEntity

@Database(
    version = 1,
    entities = [
        PlaceEntity::class,
        CategoryEntity::class,
        RuleEntity::class,
    ],
    exportSchema = true,
)
@TypeConverters(EnumConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placesDao(): PlacesRoomDao
    abstract fun rulesDao(): RulesRoomDao
    abstract fun categoriesDao(): CategoriesRoomDao
}
