package com.haidoan.android.stren.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.haidoan.android.stren.core.model.training.Coordinate

@Entity(
    tableName = "coordinate",
)
data class CoordinateEntity(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo val latitude: Double,
    @ColumnInfo val longitude: Double,
    @ColumnInfo val distanceTravelled: Float = 0f
)

fun CoordinateEntity.asExternalModel() = Coordinate(
    timestamp = timestamp, latitude = latitude, longitude = longitude
)
