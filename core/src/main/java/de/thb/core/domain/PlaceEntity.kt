package de.thb.core.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "places")
data class PlaceEntity(

    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "title")
    val name: String,
)