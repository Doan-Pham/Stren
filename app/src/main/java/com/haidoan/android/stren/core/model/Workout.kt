package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import java.text.DecimalFormat
import java.time.LocalDate
import java.util.*

data class Workout(
    @DocumentId
    val id: String,
    val name: String,
    val note: String,
    val date: LocalDate,
    val trainedExercises: List<TrainedExercise>
)

/**
 * [id] property: An id solely for differentiating TrainedExercise objects for update purposes
 */
data class TrainedExercise(
    val id: UUID = UUID.randomUUID(),
    val exercise: Exercise,
    val trainingSets: List<TrainingMeasurementMetrics>
) {
    override fun toString(): String =
        "TrainedExercise(exerciseName: ${exercise.name}; trainingSets: $trainingSets"
}

fun Exercise.asTrainedExerciseWithOneSet(): TrainedExercise {
    val trainingSets: MutableList<TrainingMeasurementMetrics> = mutableListOf()

    when (this.belongedCategory) {
        ExerciseCategoryWithSpecialMetrics.CARDIO.fieldValue -> {
            trainingSets.add(
                TrainingMeasurementMetrics.DistanceAndDuration(
                    0.0,
                    0.0
                )
            )

        }
        ExerciseCategoryWithSpecialMetrics.STRETCHING.fieldValue -> {
            trainingSets.add(TrainingMeasurementMetrics.DurationOnly(0L))
        }
        else -> {
            trainingSets.add(TrainingMeasurementMetrics.WeightAndRep(0.0, 0L))
        }
    }
    return TrainedExercise(
        exercise = Exercise(
            id = this.id,
            name = this.name,
            belongedCategory = this.belongedCategory
        ), trainingSets = trainingSets.toList()
    )
}

sealed class TrainingMeasurementMetrics {
    val id: UUID = UUID.randomUUID()

    abstract override fun toString(): String
    data class WeightAndRep(
        val weight: Double,
        val repAmount: Long
    ) : TrainingMeasurementMetrics() {
        override fun toString(): String {
            return "$weight kg x $repAmount"
        }
    }

    data class DurationOnly(val seconds: Long) :
        TrainingMeasurementMetrics() {
        override fun toString(): String {
            if (seconds > 3600) return "${seconds / 3600} hrs"
            if (seconds > 60) return "${seconds / 60} mins"
            return "${seconds}s"
        }
    }

    data class DistanceAndDuration(val kilometers: Double, val hours: Double) :
        TrainingMeasurementMetrics() {
        override fun toString(): String {
            val distance = if (kilometers < 1) "${kilometers * 1000}m" else "${kilometers}km"
            val duration =
                if (hours < (1 / 60)) {
                    "${(hours * 3600).toLong()}s"
                } else if (hours < 1) {
                    "${(hours * 60).toLong()} mins"
                } else {
                    "${DecimalFormat("#.#").format(hours)} hrs"
                }
            return "$distance x $duration"
        }
    }
}

enum class ExerciseCategoryWithSpecialMetrics(val fieldValue: String) {
    CARDIO("Cardio"), STRETCHING("Stretching"),
}