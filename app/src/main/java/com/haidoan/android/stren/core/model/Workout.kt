package com.haidoan.android.stren.core.model

import com.google.firebase.firestore.DocumentId
import java.text.DecimalFormat
import java.time.LocalDate

data class Workout(
    @DocumentId
    val id: String,
    val name: String,
    val note: String,
    val date: LocalDate,
    val trainedExercises: List<TrainedExercise>
)

data class TrainedExercise(
    val exercise: Exercise,
    val trainingSets: List<TrainingMeasurementMetrics>
)

sealed interface TrainingMeasurementMetrics {
    override fun toString(): String
    data class WeightAndRep(val weight: String, val repAmount: Long) : TrainingMeasurementMetrics {
        override fun toString(): String {
            return "$weight x $repAmount"
        }
    }

    data class DurationOnly(val seconds: Long) : TrainingMeasurementMetrics {
        override fun toString(): String {
            if (seconds > 3600) return "${seconds / 3600} hrs"
            if (seconds > 60) return "${seconds / 60} mins"
            return "${seconds}s"
        }
    }

    data class DistanceAndDuration(val kilometers: Long, val hours: Double) :
        TrainingMeasurementMetrics {
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
