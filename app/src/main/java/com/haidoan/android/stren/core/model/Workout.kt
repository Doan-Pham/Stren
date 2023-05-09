package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId

data class Workout(
    @DocumentId
    val id: String,
    val name: String,
    val note: String,
    val trainedExercises: List<TrainedExercise>
)

data class TrainedExercise(
    val exercise: Exercise,
    val trainingSets: List<TrainingMeasurementMetrics>
)

sealed interface TrainingMeasurementMetrics {
    data class WeightAndRep(val weight: String, val repAmount: Long) : TrainingMeasurementMetrics
    data class DurationOnly(val seconds: Long) : TrainingMeasurementMetrics
    data class DistanceAndDuration(val kilometers: Long, val hours: Double) :
        TrainingMeasurementMetrics
}
