package com.haidoan.android.stren.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.haidoan.android.stren.core.database.dao.CoordinateDao
import com.haidoan.android.stren.core.database.model.CoordinateEntity

@Database(
    entities = [
        CoordinateEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
abstract class StrenDatabase : RoomDatabase() {
    abstract fun coordinateDao(): CoordinateDao
}
