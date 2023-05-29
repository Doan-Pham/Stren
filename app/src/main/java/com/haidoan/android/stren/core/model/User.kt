package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import java.time.LocalDate

data class User(
    @DocumentId
    val id: String,
    val email: String,
    val trackedCategories: List<TrackedCategory>
)

sealed class TrackedCategory {
    abstract val dataSourceId: String
    abstract val startDate: LocalDate
    abstract val endDate: LocalDate

    data class Calories(
        override val dataSourceId: String,
        override val startDate: LocalDate,
        override val endDate: LocalDate
    ) : TrackedCategory()


    data class ExerciseOneRepMax(
        override val dataSourceId: String,
        override val startDate: LocalDate,
        override val endDate: LocalDate,
        val exerciseId: String
    ) : TrackedCategory()
}