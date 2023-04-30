package com.haidoan.android.stren.core.repository

import androidx.paging.PagingData
import com.haidoan.android.stren.core.model.Exercise
import kotlinx.coroutines.flow.Flow

interface ExercisesRepository {
    fun getAllExercisesStream(): Flow<PagingData<Exercise>>
}