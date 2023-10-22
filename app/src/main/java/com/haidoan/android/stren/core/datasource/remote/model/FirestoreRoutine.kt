package com.haidoan.android.stren.core.datasource.remote.model

import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.model.Routine
import timber.log.Timber


internal fun DocumentSnapshot.toRoutine(): Routine {
    Timber.d("document: $this")
    val routinesData = this.toObject(FirestoreRoutine::class.java)
    Timber.d("toRoutine() - routinesData: $routinesData")
    return routinesData?.asRoutine() ?: Routine(name = "Undefined", trainedExercises = listOf())
}

internal fun QuerySnapshot.toRoutines(): List<Routine> =
    this.documents.mapNotNull { document ->
        Timber.d("document: $document")
        val routinesData = document.toObject(FirestoreRoutine::class.java)
        Timber.d("toFirestoreRoutine() - : $routinesData")
        routinesData?.asRoutine()
    }

/**
 * Firestore representation of [Routine] class
 */
internal data class FirestoreRoutine(
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