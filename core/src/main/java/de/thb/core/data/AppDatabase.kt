package de.thb.core.data

import androidx.room.Database
import androidx.room.RoomDatabase
import de.thb.core.data.places.local.PlacesLocalDataSource
import de.thb.core.domain.PlaceEntity

@Database(version = 1, entities = [PlaceEntity::class])
abstract class AppDatabase : RoomDatabase() {
    abstract fun placesLocalDataSource(): PlacesLocalDataSource
}
