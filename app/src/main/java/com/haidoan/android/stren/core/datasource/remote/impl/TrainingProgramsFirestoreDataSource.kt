package com.haidoan.android.stren.core.datasource.remote.impl

import com.google.firebase.firestore.FirebaseFirestore
import com.haidoan.android.stren.core.datasource.remote.TRAINING_PROGRAMS_COLLECTION_PATH
import com.haidoan.android.stren.core.datasource.remote.model.FirestoreTrainingProgram
import com.haidoan.android.stren.core.model.TrainingProgram
import kotlinx.coroutines.tasks.await
import javax.inject.Inject


class TrainingProgramsFirestoreDataSource @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun addTrainingProgram(userId: String, trainingProgram: TrainingProgram): String =
        firestore.collection(TRAINING_PROGRAMS_COLLECTION_PATH)
            .add(FirestoreTrainingProgram.from(userId, trainingProgram))
            .await().id
}