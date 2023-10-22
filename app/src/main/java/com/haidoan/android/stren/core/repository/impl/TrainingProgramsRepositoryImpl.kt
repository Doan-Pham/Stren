package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.impl.TrainingProgramsFirestoreDataSource
import com.haidoan.android.stren.core.model.TrainingProgram
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import timber.log.Timber
import java.time.LocalDate
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

    fun getTrainingProgramsStreamByUserIdAndDate(
        userId: String,
        date: LocalDate,
    ): Flow<List<TrainingProgram>> {
        Timber.d("getTrainingProgramsStreamByUserIdAndDate() is called; userId: $userId,date: $date ")

        return trainingProgramRemoteDataSource
            .getTrainingProgramsStreamByUserIdAfterDate(userId, date)
            .map { programs ->
                Timber.d("programs: $programs")
                programs.filter {
                    !date.isAfter(it.endDate)
                }
            }
            .catch {
                Timber.e("getTrainingProgramsStreamByUserIdAndDate() - Exception: $it ${it.printStackTrace()}")
            }
    }

    fun getTrainingProgramsStreamByUserId(userId: String): Flow<List<TrainingProgram>> {
        Timber.d("getTrainingProgramsStreamByUserId() is called; userId: $userId ")

        return trainingProgramRemoteDataSource
            .getTrainingProgramsStreamByUserIdAfterDate(userId)
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