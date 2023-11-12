package com.haidoan.android.stren.core.platform.android

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat

fun Context.hasLocationPermission(): Boolean {
    val accessCoarseLocationPermission =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)

    val accessFineLocationPermission =
        ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

    return accessCoarseLocationPermission == PackageManager.PERMISSION_GRANTED &&
            accessFineLocationPermission == PackageManager.PERMISSION_GRANTED
}