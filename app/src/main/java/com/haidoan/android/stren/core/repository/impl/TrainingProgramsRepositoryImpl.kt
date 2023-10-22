package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.impl.TrainingProgramsFirestoreDataSource
import com.haidoan.android.stren.core.model.TrainingProgram
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
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

    fun getTrainingProgramsStreamByUserId(userId: String): Flow<List<TrainingProgram>> {
        Timber.d("getTrainingProgramsStreamByUserId() is called; userId: $userId ")

        return trainingProgramRemoteDataSource
            .getTrainingProgramsStreamByUserIdAndDate(userId)
            .catch {
                Timber.e("getTrainingProgramsStreamByUserId() - Exception: $it ${it.printStackTrace()}")
            }
    }

    suspend fun deleteTrainingProgram(trainingProgramId: String) {
        try {
            Timber.d("deleteTrainingProgram() -  trainingProgramId: $trainingProgramId")
            trainingProgramRemoteDataSource.deleteTrainingProgram(trainingProgramId)
        } catch (exception: Exception) {
            Timber.e("deleteTrainingProgram() - Exception: $exception ${exception.printStackTrace()}")
        }
    }
}