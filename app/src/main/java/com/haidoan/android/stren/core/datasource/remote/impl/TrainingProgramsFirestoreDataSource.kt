package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.snapshots
import com.haidoan.android.stren.core.datasource.remote.TRAINING_PROGRAMS_COLLECTION_PATH
import com.haidoan.android.stren.core.datasource.remote.model.FirestoreTrainingProgram
import com.haidoan.android.stren.core.datasource.remote.model.toTrainingProgramsList
import com.haidoan.android.stren.core.model.TrainingProgram
import com.haidoan.android.stren.core.utils.DateUtils.toTimeStampDayEnd
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.tasks.await
import java.time.LocalDate
import javax.inject.Inject


class TrainingProgramsFirestoreDataSource @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addTrainingProgram(userId: String, trainingProgram: TrainingProgram): String =
        firestore.collection(TRAINING_PROGRAMS_COLLECTION_PATH)
            .add(FirestoreTrainingProgram.from(userId, trainingProgram))
            .await().id

    fun getTrainingProgramsStreamByUserIdAfterDate(
        userId: String,
        date: LocalDate,
    ): Flow<List<TrainingProgram>> =
        firestore.collection(TRAINING_PROGRAMS_COLLECTION_PATH)
            .whereEqualTo("userId", userId)
            .whereLessThanOrEqualTo("startDate", date.toTimeStampDayEnd())
            .snapshots()
            .mapNotNull { it.toTrainingProgramsList() }

    fun getTrainingProgramsStreamByUserIdAfterDate(userId: String): Flow<List<TrainingProgram>> =
        firestore.collection(TRAINING_PROGRAMS_COLLECTION_PATH)
            .whereEqualTo("userId", userId)
            .snapshots()
            .mapNotNull { it.toTrainingProgramsList() }

    suspend fun deleteTrainingProgram(trainingProgramId: String) {
        firestore.collection(TRAINING_PROGRAMS_COLLECTION_PATH)
            .document(trainingProgramId).delete().await()
    }
}