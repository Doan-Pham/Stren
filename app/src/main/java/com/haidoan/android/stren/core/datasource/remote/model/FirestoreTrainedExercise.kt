package com.haidoan.android.stren.core.datasource.remote.model

import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics.ExerciseCategoryWithSpecialMetrics.CARDIO
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics.ExerciseCategoryWithSpecialMetrics.STRETCHING
import timber.log.Timber

/**
 * "trainingSet" field in Firestore is of type Map, which doesn't allow duplicated keys, but
 * this entity should domain-wise. To solve this, the "trainingSet" field from application's model classes
 * will have their key attached with this separator char and an index to differentiate among each
 * other (The separator is also for parsing purposes)
 */
private const val DEFAULT_SEPARATOR_CHAR = '_'

/**
 * This is for the case where application doesn't have any need for the Firestore's "trainingSet" field's
 * key, but still need to differentiate between fields with duplicated keys
 */
private const val UNDEFINED_KEY = "Undefined"

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
        val firestoreTrainingSets = this.trainingSets.toSortedMap()
        val trainingSets: MutableList<TrainingMeasurementMetrics> = mutableListOf()

        when (this.exerciseCategory) {
            CARDIO.fieldValue -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(
                        TrainingMeasurementMetrics.DistanceAndDuration(
                            it.key
                                .substringAfter(DEFAULT_SEPARATOR_CHAR)
                                .substringBefore(DEFAULT_SEPARATOR_CHAR)
                                .toDouble(),
                            it.value
                        )
                    )
                }
            }
            STRETCHING.fieldValue -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(TrainingMeasurementMetrics.DurationOnly(it.value.toLong()))
                }
            }
            else -> {
                firestoreTrainingSets.forEach {
                    trainingSets.add(
                        TrainingMeasurementMetrics.WeightAndRep(
                            it.key
                                .substringAfter(DEFAULT_SEPARATOR_CHAR)
                                .substringBefore(DEFAULT_SEPARATOR_CHAR)
                                .toDouble(),
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
            val iteratedMetricsFrequency = mutableMapOf<String, Int>()
            val firestoreTrainingSets: MutableMap<String, Double> = mutableMapOf()

            when (trainedExercise.exercise.belongedCategory) {
                CARDIO.fieldValue -> {
                    iteratedMetricsFrequency.clear()

                    trainingSets.forEachIndexed { index, trainingSet ->
                        val metrics = trainingSet as TrainingMeasurementMetrics.DistanceAndDuration
                        val kilometers = metrics.kilometers.toString()
                        val hours = metrics.hours

                        iteratedMetricsFrequency[kilometers] =
                            (iteratedMetricsFrequency[kilometers] ?: 0) + 1

                        val metricsKeyWithIndex =
                            (index + 1).toString() + DEFAULT_SEPARATOR_CHAR +
                                    kilometers + DEFAULT_SEPARATOR_CHAR +
                                    iteratedMetricsFrequency[kilometers]
                        firestoreTrainingSets[metricsKeyWithIndex] = hours

                        Timber.d("from(trainedExercise: TrainedExercise) - CARDIO - metricsKeyWithIndex:$metricsKeyWithIndex")
                    }
                }
                STRETCHING.fieldValue -> {
                    trainingSets.forEachIndexed { index, metrics ->
                        val metricsValue = metrics as TrainingMeasurementMetrics.DurationOnly
                        firestoreTrainingSets[UNDEFINED_KEY + DEFAULT_SEPARATOR_CHAR + (index + 1).toString()] =
                            metricsValue.seconds.toDouble()
                    }
                }
                else -> {
                    iteratedMetricsFrequency.clear()
                    trainingSets.forEachIndexed { index, trainingSet ->
                        val metrics = trainingSet as TrainingMeasurementMetrics.WeightAndRep
                        val weight = metrics.weight.toString()
                        val repAmount = metrics.repAmount

                        iteratedMetricsFrequency[weight] =
                            (iteratedMetricsFrequency[weight] ?: 0) + 1

                        val metricsKeyWithIndex =
                            (index + 1).toString() + DEFAULT_SEPARATOR_CHAR +
                                    weight + DEFAULT_SEPARATOR_CHAR +
                                    iteratedMetricsFrequency[weight]
                        firestoreTrainingSets[metricsKeyWithIndex] = repAmount.toDouble()

                        Timber.d("from(trainedExercise: TrainedExercise) - WEIGHT AND REP - metrics:$metrics")
                        Timber.d("from(trainedExercise: TrainedExercise) - WEIGHT AND REP - metricsKeyWithIndex:$metricsKeyWithIndex")
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