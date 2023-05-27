package com.haidoan.android.stren.core.datasource.remote.model

import com.google.firebase.Timestamp
import com.haidoan.android.stren.core.model.CaloriesOfDate
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate

internal data class FirestoreCaloriesOfDate(
    val totalCalories: Int = 0,
    val date: Timestamp = Timestamp.now()
)

internal fun FirestoreCaloriesOfDate.toExternalModel(): CaloriesOfDate =
    CaloriesOfDate(calories = totalCalories, date = date.toLocalDate())
