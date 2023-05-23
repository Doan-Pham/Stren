package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.base.RoutinesRemoteDataSource
import com.haidoan.android.stren.core.datasource.remote.model.FirestoreTrainedExercise
import com.haidoan.android.stren.core.model.Routine
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import javax.inject.Inject

private const val ROUTINE_COLLECTION_PATH = "Routine"
private const val USER_COLLECTION_PATH = "User"

class RoutinesFirestoreDataSource @Inject constructor() : RoutinesRemoteDataSource {
    private val firestore = FirebaseFirestore.getInstance()

    override suspend fun getRoutinesStreamByUserId(userId: String): Flow<List<Routine>> =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH").snapshots()
            .mapNotNull { it.toRoutines() }

    override suspend fun getRoutinesByUserId(userId: String): List<Routine> =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH").get().await()
            .toRoutines()

    override suspend fun getRoutineById(userId: String, routineId: String): Routine =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH")
            .document(routineId).get().await().toRoutine()

    override suspend fun addRoutine(userId: String, routine: Routine): String =
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH")
            .add(FirestoreRoutine.from(routine))
            .await().id

    override suspend fun updateRoutine(userId: String, routine: Routine) {
        firestore.collection("$USER_COLLECTION_PATH/$userId/$ROUTINE_COLLECTION_PATH")
            .document(routine.id)
            .set(FirestoreRoutine.from(routine))
            .await()
    }

    private fun DocumentSnapshot.toRoutine(): Routine {
        Timber.d("document: $this")
        val routinesData = this.toObject(FirestoreRoutine::class.java)
        Timber.d("toFirestoreRoutine() - : $routinesData")
        return routinesData?.asRoutine() ?: Routine(name = "Undefined", trainedExercises = listOf())
    }

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
                trainedExercises = trainedExercises
            )
        }

        companion object {
            fun from(routine: Routine): FirestoreRoutine {
                return FirestoreRoutine(
                    id = routine.id,
                    name = routine.name,
                    trainedExercises = routine.trainedExercises.map {
                        FirestoreTrainedExercise.from(
                            it
                        )
                    }
                )
            }
        }
    }
}

