package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.training.Coordinate
import kotlinx.coroutines.flow.Flow

interface CoordinatesRepository {
    fun getCoordinates(): Flow<List<Coordinate>>

    suspend fun insertCoordinate(coordinate: Coordinate)

    suspend fun deleteAllCoordinates()

    /**
     * Return the total travelled distance in METERS
     */
    fun getTotalDistanceTravelled(): Flow<Float?>
}