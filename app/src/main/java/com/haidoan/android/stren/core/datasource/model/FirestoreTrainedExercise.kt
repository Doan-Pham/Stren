package com.haidoan.android.stren.core.datasource.model

import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategoryWithSpecialMetrics
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import timber.log.Timber

/**
 * Firestore representation of [TrainedExercise] class
 */
internal data class FirestoreTrainedExercise(
    val exerciseCategory: String = "Undefined",
    val exerciseId: String = "Undefined",
    val name: String = "Undefined",
    val note: String = "Undefined",
    val trainingSets: Map<String, Long> = mapOf(),
) {
    fun asTrainedExercise(): TrainedExercise {
        val firestoreTrainingSets = this.trainingSets
        val trainingSets: MutableList<TrainingMeasurementMetrics> = mutableListOf()

        when (this.exerciseCategory) {
            ExerciseCategoryWithSpecialMetrics.CARDIO.fieldValue -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(
                        TrainingMeasurementMetrics.DistanceAndDuration(
                            it.key.toLong(),
                            it.value.toDouble()
                        )
                    )
                }
            }
            ExerciseCategoryWithSpecialMetrics.STRETCHING.fieldValue -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(TrainingMeasurementMetrics.DurationOnly(it.value))
                }
            }
            else -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(TrainingMeasurementMetrics.WeightAndRep(it.key, it.value))
                }
            }
        }
        Timber.d("trainingSets: $trainingSets")
        return TrainedExercise(
            exercise = Exercise(
                id = this.exerciseId,
                name = this.name,
                belongedCategory = this.exerciseCategory
            ), trainingSets = trainingSets.toList()
        )
    }
}