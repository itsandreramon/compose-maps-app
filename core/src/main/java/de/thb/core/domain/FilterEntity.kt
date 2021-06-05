package de.thb.core.domain

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.UUID

@Entity(tableName = "filters")
data class FilterEntity(

    @PrimaryKey
    val uuid: String = UUID.randomUUID().toString(),

    @ColumnInfo(name = "name")
    val name: String,

    @ColumnInfo(name = "severity")
    val severity: Severity,

    @ColumnInfo(name = "description")
    val description: String? = "",
)
