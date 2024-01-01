package com.haidoan.android.stren.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.haidoan.android.stren.core.database.model.CoordinateEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface CoordinateDao {
    @Query(
        value = """
            SELECT * FROM coordinate
            ORDER BY timestamp DESC
    """,
    )
    fun getCoordinates(): Flow<List<CoordinateEntity>>

    @Query("""
        SELECT SUM(distanceTravelled) 
        FROM coordinate
    """)
    fun getTotalDistanceTravelled(): Flow<Float?>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrIgnoreCoordinate(coordinates: CoordinateEntity): Long

    @Query("""
        SELECT * 
        FROM coordinate 
        ORDER BY timestamp DESC 
        LIMIT 1
    """)
    fun getLatestCoordinate(): CoordinateEntity?

    @Query(
        value = """
            DELETE FROM coordinate
        """,
    )
    suspend fun deleteAllCoordinates()
}
