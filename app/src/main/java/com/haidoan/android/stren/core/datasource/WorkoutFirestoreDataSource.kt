package com.haidoan.android.stren.core.datasource

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.datasource.model.FirestoreTrainedExercise
import com.haidoan.android.stren.core.model.Workout
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayEnd
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart
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
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .whereEqualTo("userId", userId)
            .whereGreaterThanOrEqualTo("date", date.toTimeStampDayStart())
            .whereLessThanOrEqualTo("date", date.toTimeStampDayEnd())
            .get().await().toWorkoutsList()

    /**
     * TODO: Implement Pagination to fetch only a certain amount of workouts
     */
    override suspend fun getDatesThatHaveWorkoutByUserId(userId: String): List<LocalDate> =
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .whereEqualTo("userId", userId).get().await().toWorkoutsList().map { it.date }

    override suspend fun addWorkout(userId: String, workout: Workout): String =
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .add(FirestoreWorkout.from(userId, workout))
            .await().id


    private fun QuerySnapshot.toWorkoutsList(): List<Workout> =
        this.documents.mapNotNull { document ->
            Timber.d("document: $document")
            val workoutData = document.toObject(FirestoreWorkout::class.java)
            Timber.d("toFirestoreWorkout() - : $workoutData")
            workoutData?.asWorkout()
        }


    /**
     * Firestore representation of [Workout] class
     */
    private data class FirestoreWorkout(
        @DocumentId
        val id: String = "Undefined",
        val date: Timestamp = Timestamp.now(),
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
                date = this.date.toLocalDate(),
                trainedExercises = trainedExercises
            )
        }

        companion object {
            fun from(userId: String, workout: Workout): FirestoreWorkout {
                return FirestoreWorkout(
                    id = workout.id,
                    name = workout.name,
                    trainedExercises = workout.trainedExercises.map {
                        FirestoreTrainedExercise.from(
                            it
                        )
                    },
                    userId = userId,
                    date = workout.date.toTimeStampDayStart()
                )
            }
        }
    }
}

