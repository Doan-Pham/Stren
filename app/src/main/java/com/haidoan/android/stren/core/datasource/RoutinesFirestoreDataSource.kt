package com.haidoan.android.stren.core.datasource

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.Routine
import com.haidoan.android.stren.core.model.TrainedExercise
import com.haidoan.android.stren.core.model.TrainingMeasurementMetrics
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private const val ROUTINE_COLLECTION_PATH = "Routine"
private const val USER_COLLECTION_PATH = "User"

class RoutinesFirestoreDataSource @Inject constructor() : RoutinesRemoteDataSource {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getRoutinesByUserId(userId: String): List<Routine> =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH").get()
            .await().toRoutines()


    private fun QuerySnapshot.toRoutines(): List<Routine> =
        this.documents.mapNotNull { document ->
            Timber.d("document: $document")
            val routinesData = document.toObject(FirestoreRoutine::class.java)
            Timber.d("toFirestoreRoutine() - : $routinesData")
            routinesData?.asRoutine()
        }

    /**
     * Firestore representation of [Routine] class
     */
    private data class FirestoreRoutine(
        @DocumentId
        val id: String = "Undefined",
        val name: String = "Undefined",
        val note: String = "Undefined",
        val trainedExercises: List<FirestoreTrainedExercise> = listOf(),
    ) {

        fun asRoutine(): Routine {
            val trainedExercises = this.trainedExercises.map { firestoreTrainedExercise ->
                firestoreTrainedExercise.asTrainedExercise()
            }
            Timber.d("trainedExercises: $trainedExercises")
            return Routine(
                id = this.id,
                name = this.name,
                note = this.note,
                trainedExercises = trainedExercises
            )
        }
    }

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
}

