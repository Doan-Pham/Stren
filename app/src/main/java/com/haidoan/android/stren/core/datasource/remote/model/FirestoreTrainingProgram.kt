package com.haidoan.android.stren.core.datasource.remote.model

import com.google.firebase.Timestamp
import com.google.firebase.firestore.DocumentId
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.haidoan.android.stren.core.model.TrainingProgram
import com.haidoan.android.stren.core.utils.DateUtils.toLocalDate
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayEnd
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayStart
import timber.log.Timber


internal fun DocumentSnapshot.toTrainingProgram(): TrainingProgram {
    Timber.d("document: $this")
    val data = this.toObject(FirestoreTrainingProgram::class.java)
    Timber.d("toFirestoreTrainingProgram() - : $data")
    return data?.asTrainingProgram() ?: TrainingProgram.dummy()
}

internal fun QuerySnapshot.toTrainingProgramsList(): List<TrainingProgram> =
    this.documents.mapNotNull { document ->
        Timber.d("document: $document")
        val trainingProgramData = document.toObject(FirestoreTrainingProgram::class.java)
        Timber.d("toFirestoreTrainingProgram() - : $trainingProgramData")
        trainingProgramData?.asTrainingProgram()
    }


/**
 * Firestore representation of [TrainingProgram] class
 * @param routinesIds Solely for querying purpose, since Firestore's array-contains query works well for simple array, and not object array like [routines]
 */
internal data class FirestoreTrainingProgram(
    @DocumentId
    val id: String = "Undefined",
    val name: String = "Undefined",
    val totalNumOfDay: Int = 0,
    val startDate: Timestamp = Timestamp.now(),
    val endDate: Timestamp = Timestamp.now(),
    val routinesByDayOffset: Map<String, List<FirestoreRoutine>> = mapOf(),
    val userId: String = "Undefined",
) {
    fun asTrainingProgram(): TrainingProgram {
        val routinesByDayOffset = this.routinesByDayOffset.mapValues { firestoreRoutines ->
            firestoreRoutines.value.map { it.asRoutine() }
        }
        val result = TrainingProgram(
            id = this.id,
            name = this.name,
            startDate = this.startDate.toLocalDate(),
            endDate = this.endDate.toLocalDate(),
            totalNumOfDay = totalNumOfDay,
            routinesByDayOffset = routinesByDayOffset.mapKeys { it.key.toInt() }
        )
        Timber.d("result: $result")

        return result
    }

    companion object {
        fun from(userId: String, trainingProgramData: TrainingProgram): FirestoreTrainingProgram {
            return FirestoreTrainingProgram(
                id = trainingProgramData.id,
                name = trainingProgramData.name,
                routinesByDayOffset = trainingProgramData.routinesByDayOffset.mapKeys { it.key.toString() }
                    .mapValues { routinesByDayOffset ->
                        routinesByDayOffset.value.map { FirestoreRoutine.from(it) }
                    },
                totalNumOfDay = trainingProgramData.totalNumOfDay,
                userId = userId,
                startDate = trainingProgramData.startDate.toTimeStampDayStart(),
                endDate = trainingProgramData.endDate.toTimeStampDayEnd()
            )
        }
    }
}