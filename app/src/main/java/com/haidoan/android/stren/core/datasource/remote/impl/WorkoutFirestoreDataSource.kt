package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.WorkoutRemoteDataSource
import com.haidoan.android.stren.core.datasource.remote.model.FirestoreTrainedExercise
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.Workout
import com.haidoan.android.stren.core.model.getExerciseOneRepMaxes
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayEnd
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart
import com.haidoan.android.stren.core.utils.ListUtils.mergeLists
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
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

    override suspend fun getAllExercisesTrained(userId: String): List<TrainedExercise> =
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .whereEqualTo("userId", userId).get().await().toWorkoutsList()
            .map { it.trainedExercises }.fold(
                listOf()
            ) { firstList, secondList ->
                mergeLists(
                    firstList = firstList,
                    secondList = secondList,
                    areEqual = { ex1, ex2 -> ex1.exercise.id == ex2.exercise.id },
                    handleConflict = { _, ex2 ->
                        ex2
                    })
            }

    override fun getExerciseOneRepMaxesStream(
        userId: String,
        exerciseId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<Map<LocalDate, Float>> =
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .whereEqualTo("userId", userId)
            .whereArrayContains("trainedExercisesIds", exerciseId)
            .whereGreaterThanOrEqualTo("date", startDate.toTimeStampDayStart())
            .whereLessThanOrEqualTo("date", endDate.toTimeStampDayEnd())
            .snapshots()
            .map {
                Timber.d("snapshots() is fine")
                it.toWorkoutsList().getExerciseOneRepMaxes(exerciseId)
            }


    override suspend fun addWorkout(userId: String, workout: Workout): String =
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .add(FirestoreWorkout.from(userId, workout))
            .await().id

    override suspend fun getWorkoutById(workoutId: String): Workout =
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .document(workoutId).get().await().toWorkout()

    override suspend fun updateWorkout(userId: String, workout: Workout) {
        firestore.collection(WORKOUT_COLLECTION_PATH)
            .document(workout.id)
            .set(FirestoreWorkout.from(userId, workout))
            .await()
    }

    private fun DocumentSnapshot.toWorkout(): Workout {
        Timber.d("document: $this")
        val data = this.toObject(FirestoreWorkout::class.java)
        Timber.d("toFirestoreWorkout() - : $data")
        return data?.asWorkout() ?: Workout(
            name = "Undefined",
            trainedExercises = listOf(),
            date = LocalDate.of(1900, 12, 12)
        )
    }

    private fun QuerySnapshot.toWorkoutsList(): List<Workout> =
        this.documents.mapNotNull { document ->
            Timber.d("document: $document")
            val workoutData = document.toObject(FirestoreWorkout::class.java)
            Timber.d("toFirestoreWorkout() - : $workoutData")
            workoutData?.asWorkout()
        }


    /**
     * Firestore representation of [Workout] class
     * @param trainedExercisesIds Solely for querying purpose, since Firestore's array-contains query works well for simple array, and not object array like [trainedExercises]
     */
    private data class FirestoreWorkout(
        @DocumentId
        val id: String = "Undefined",
        val date: Timestamp = Timestamp.now(),
        val name: String = "Undefined",
        val note: String = "Undefined",
        val trainedExercises: List<FirestoreTrainedExercise> = listOf(),
        val trainedExercisesIds: List<String> = trainedExercises.map { it.exerciseId },
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

