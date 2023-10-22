package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.impl.TrainingProgramsFirestoreDataSource
import com.haidoan.android.stren.core.model.TrainingProgram
import timber.log.Timber
import javax.inject.Inject

class TrainingProgramsRepositoryImpl @Inject constructor(
    private val trainingProgramRemoteDataSource: TrainingProgramsFirestoreDataSource,
) {
    suspend fun addTrainingProgram(userId: String, trainingProgram: TrainingProgram): String {
        return try {
            val newTrainingProgramId = trainingProgramRemoteDataSource.addTrainingProgram(userId, trainingProgram)

            Timber.d("addTrainingProgram() - Success, new trainingProgram id: $newTrainingProgramId")

            newTrainingProgramId
        } catch (exception: Exception) {
            Timber.e("addTrainingProgram() - Exception: ${exception.message}")
            "Undefined TrainingProgram ID"
        }
    }
}