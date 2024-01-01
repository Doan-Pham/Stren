package com.haidoan.android.stren.core.repository.mapper

import com.haidoan.android.stren.core.database.model.CoordinateEntity
import com.haidoan.android.stren.core.model.training.Coordinate

fun Coordinate.toEntity() = CoordinateEntity(timestamp, latitude, longitude)