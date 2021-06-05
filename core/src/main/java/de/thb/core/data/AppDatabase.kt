package de.thb.core.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import de.thb.core.data.filters.local.FiltersRoomDao
import de.thb.core.data.places.local.PlacesRoomDao
import de.thb.core.domain.FilterEntity
import de.thb.core.domain.PlaceEntity

@Database(
    version = 4,
    entities = [
        PlaceEntity::class,
        FilterEntity::class
    ],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
        AutoMigration(from = 3, to = 4),
    ],
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placesDao(): PlacesRoomDao
    abstract fun filtersDao(): FiltersRoomDao
}
