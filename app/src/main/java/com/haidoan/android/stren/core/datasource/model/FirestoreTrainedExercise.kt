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
    val trainingSets: Map<String, Double> = mapOf(),
) {
    fun asTrainedExercise(): TrainedExercise {
        val firestoreTrainingSets = this.trainingSets
        val trainingSets: MutableList<TrainingMeasurementMetrics> = mutableListOf()

        when (this.exerciseCategory) {
            ExerciseCategoryWithSpecialMetrics.CARDIO.fieldValue -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(
                        TrainingMeasurementMetrics.DistanceAndDuration(
                            it.key.toDouble(),
                            it.value.toDouble()
                        )
                    )
                }
            }
            ExerciseCategoryWithSpecialMetrics.STRETCHING.fieldValue -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(TrainingMeasurementMetrics.DurationOnly(it.value.toLong()))
                }
            }
            else -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(
                        TrainingMeasurementMetrics.WeightAndRep(
                            it.key.toDouble(),
                            it.value.toLong()
                        )
                    )
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

    companion object {
        fun from(trainedExercise: TrainedExercise): FirestoreTrainedExercise {
            val trainingSets = trainedExercise.trainingSets
            val firestoreTrainingSets: MutableMap<String, Double> = mutableMapOf()

            when (trainedExercise.exercise.belongedCategory) {
                ExerciseCategoryWithSpecialMetrics.CARDIO.fieldValue -> {
                    trainingSets.forEach {
                        val metrics = it as TrainingMeasurementMetrics.DistanceAndDuration
                        firestoreTrainingSets[metrics.kilometers.toString()] = metrics.hours
                    }
                }
                ExerciseCategoryWithSpecialMetrics.STRETCHING.fieldValue -> {
                    trainingSets.forEachIndexed { index, metrics ->
                        val metricsValue = metrics as TrainingMeasurementMetrics.DurationOnly
                        firestoreTrainingSets["set ${index + 1}"] = metricsValue.seconds.toDouble()
                    }
                }
                else -> {
                    trainingSets.forEach {
                        val metrics = it as TrainingMeasurementMetrics.WeightAndRep
                        firestoreTrainingSets[metrics.weight.toString()] =
                            metrics.repAmount.toDouble()
                    }
                }
            }

            return FirestoreTrainedExercise(
                exerciseId = trainedExercise.exercise.id,
                exerciseCategory = trainedExercise.exercise.belongedCategory,
                name = trainedExercise.exercise.name,
                trainingSets = firestoreTrainingSets
            )
        }
    }

}