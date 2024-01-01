package com.haidoan.android.stren.core.model.training

import android.location.Location
import kotlinx.datetime.Clock

data class Coordinate(
    val timestamp: Long,
    val latitude: Double,
    val longitude: Double,
) {
    fun distanceTo(other: Coordinate): Float {
        val thisLocation = Location("This Location").apply {
            latitude = this@Coordinate.latitude
            longitude = this@Coordinate.longitude
        }
        val otherLocation = Location("Other Location").apply {
            latitude = other.latitude
            longitude = other.longitude
        }

        return thisLocation.distanceTo(otherLocation)
    }

    companion object {
        fun from(location: Location) = Coordinate(
            timestamp = Clock.System.now().toEpochMilliseconds(),
            latitude = location.latitude,
            longitude = location.longitude
        )
    }
}


