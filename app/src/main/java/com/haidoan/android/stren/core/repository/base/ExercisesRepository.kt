package com.haidoan.android.stren.core.repository.base

import androidx.paging.PagingData
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseQueryParameters
import com.haidoan.android.stren.core.model.MuscleGroup
import kotlinx.coroutines.flow.Flow

const val DEFAULT_EXERCISE_DATA_PAGE_SIZE = 20L

interface ExercisesRepository {
    fun getExercisesWithLimit(limit: Long = DEFAULT_EXERCISE_DATA_PAGE_SIZE): Flow<PagingData<Exercise>>
    fun getExercisesByNameWithLimit(
        exerciseName: String,
        limit: Long = DEFAULT_EXERCISE_DATA_PAGE_SIZE
    ): Flow<PagingData<Exercise>>

    fun getAllExerciseCategories(): Flow<List<ExerciseCategory>>
    fun getAllMuscleGroups(): Flow<List<MuscleGroup>>

    fun searchExercises(
        filterStandards: ExerciseQueryParameters,
        resultCountLimit: Long = DEFAULT_EXERCISE_DATA_PAGE_SIZE
    ): Flow<PagingData<Exercise>>

    fun getExerciseById(exerciseId: String): Flow<Exercise>

    suspend fun getExercisesByIds(exerciseIds: List<String>): List<Exercise>
    suspend fun createCustomExercise(userId: String, exercise: Exercise)
}