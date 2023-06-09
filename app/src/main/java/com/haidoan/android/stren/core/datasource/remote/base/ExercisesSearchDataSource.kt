package com.haidoan.android.stren.core.datasource.remote.base

import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseQueryParameters
import com.haidoan.android.stren.core.repository.base.DEFAULT_EXERCISE_DATA_PAGE_SIZE

interface ExercisesSearchDataSource {
    suspend fun searchExercise(
        exerciseQueryParameters: ExerciseQueryParameters,
        dataPageSize: Long = DEFAULT_EXERCISE_DATA_PAGE_SIZE,
        dataPageIndex: Int,
    ): List<Exercise>
}