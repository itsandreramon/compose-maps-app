package de.thb.core.domain.rule

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "rules")
data class RuleEntity(

    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Int,

    @ColumnInfo(name = "category_id")
    val categoryId: Long,

    @ColumnInfo(name = "place_id")
    val placeId: String,

    @ColumnInfo(name = "status")
    val status: Int,

    @ColumnInfo(name = "restriction")
    val restriction: Int,

    @ColumnInfo(name = "text")
    val text: String,

    @ColumnInfo(name = "timestamp")
    val timestamp: String,
) : Parcelable
