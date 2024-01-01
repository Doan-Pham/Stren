package com.haidoan.android.stren.feat.training.cardio_tracking.model

import android.location.Location
import com.google.android.gms.maps.model.LatLng
import com.haidoan.android.stren.core.model.training.Coordinate

fun Coordinate.toLatitudeLongitude() = LatLng(latitude, longitude)

fun LatLng.toLocation() = Location("MyLocationProvider").apply {
    latitude = this@toLocation.latitude
    longitude = this@toLocation.longitude
}