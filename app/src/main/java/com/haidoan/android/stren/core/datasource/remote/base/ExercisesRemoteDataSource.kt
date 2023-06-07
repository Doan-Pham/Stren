package com.haidoan.android.stren.core.datasource.remote.base

import com.google.firebase.firestore.Query
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseQueryParameters
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.base.DEFAULT_EXERCISE_DATA_PAGE_SIZE
import com.haidoan.android.stren.core.repository.impl.QueryWrapper

interface ExercisesRemoteDataSource {
    /**
     * This method returns a [Query] which can be reused to build different queries instead
     * of just running the query immediately
     */
    fun getExercisesWithLimitAsQuery(limit: Long = DEFAULT_EXERCISE_DATA_PAGE_SIZE): Query

    fun getExercisesByNameAsQuery(
        exerciseName: String,
        resultCountLimit: Long = DEFAULT_EXERCISE_DATA_PAGE_SIZE
    ): Query

    suspend fun getAllExerciseCategories(): List<ExerciseCategory>
    suspend fun getAllMuscleGroups(): List<MuscleGroup>
    fun getFilteredExercisesAsQuery(
        filterStandards: ExerciseQueryParameters,
        resultCountLimit: Long
    ): QueryWrapper

    suspend fun getExerciseById(exerciseId: String): Exercise

    suspend fun getExercisesByIds(exerciseIds: List<String>): List<Exercise>
    suspend fun createCustomExercise(userId: String, exercise: Exercise)
}