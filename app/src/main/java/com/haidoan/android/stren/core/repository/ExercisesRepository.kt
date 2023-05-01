package com.haidoan.android.stren.core.repository

import androidx.paging.PagingData
import com.haidoan.android.stren.core.model.Exercise
import kotlinx.coroutines.flow.Flow

const val DEFAULT_ITEM_COUNT_LIMIT = 20L

interface ExercisesRepository {
    fun getExercisesWithLimit(limit: Long = DEFAULT_ITEM_COUNT_LIMIT): Flow<PagingData<Exercise>>
    fun getExercisesByNameWithLimit(
        exerciseName: String,
        limit: Long = DEFAULT_ITEM_COUNT_LIMIT
    ): Flow<PagingData<Exercise>>
}