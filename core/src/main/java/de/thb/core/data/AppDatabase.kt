package de.thb.core.data

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import de.thb.core.data.places.local.PlacesRoomDao
import de.thb.core.domain.PlaceEntity

@Database(
    version = 3,
    entities = [PlaceEntity::class],
    exportSchema = true,
    autoMigrations = [
        AutoMigration(from = 1, to = 2),
        AutoMigration(from = 2, to = 3),
    ]
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun placesLocalDataSource(): PlacesRoomDao
}
