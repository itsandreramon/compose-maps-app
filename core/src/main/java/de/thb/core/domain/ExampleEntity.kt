package de.thb.core.domain

import androidx.room.Entity

@Entity(tableName = "examples")
data class ExampleEntity(
    val id: Long = 0,
    val name: String = ""
)
