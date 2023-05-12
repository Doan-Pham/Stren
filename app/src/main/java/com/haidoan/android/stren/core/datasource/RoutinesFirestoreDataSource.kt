package com.haidoan.android.stren.core.datasource

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.datasource.model.FirestoreTrainedExercise
import com.haidoan.android.stren.core.model.Routine
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
}
