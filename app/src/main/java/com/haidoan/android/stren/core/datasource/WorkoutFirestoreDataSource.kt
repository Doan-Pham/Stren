package com.haidoan.android.stren.core.datasource

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import com.haidoan.android.stren.core.model.Workout
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

private const val WORKOUT_COLLECTION_PATH = "Workout"

class WorkoutFirestoreDataSource @Inject constructor() : WorkoutRemoteDataSource {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getWorkoutsByUserIdAndDate(
        userId: String,
        date: LocalDate
    ): List<Workout> =
        firestore.collection(WORKOUT_COLLECTION_PATH).get().await().toWorkoutsList()


    private fun QuerySnapshot.toWorkoutsList(): List<Workout> =
        this.documents.mapNotNull { document ->
            Timber.d("document: $document")
            val workoutData = document.toObject(FirestoreWorkout::class.java)
            Timber.d("toFirestoreWorkout() - : $workoutData")
            workoutData?.asWorkout()
        }
}

/**
 * Firestore representation of [Workout] class
 */
private data class FirestoreWorkout(
    @DocumentId
    val id: String = "Undefined",
    val name: String = "Undefined",
    val note: String = "Undefined",
    val trainedExercises: List<FirestoreTrainedExercise> = listOf(),
    val userId: String = "Undefined",
) {
    fun asWorkout(): Workout {
        val trainedExercises = this.trainedExercises.map { firestoreTrainedExercise ->
            firestoreTrainedExercise.asTrainedExercise()
        }
        Timber.d("trainedExercises: $trainedExercises")
        return Workout(
            id = this.id,
            name = this.name,
            note = this.note,
            trainedExercises = trainedExercises
        )
    }
}

/**
 * Firestore representation of [TrainedExercise] class
 */
private data class FirestoreTrainedExercise(
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

private enum class ExerciseCategoryWithSpecialMetrics(val fieldValue: String) {
    CARDIO("Cardio"), STRETCHING("Stretching"),
}