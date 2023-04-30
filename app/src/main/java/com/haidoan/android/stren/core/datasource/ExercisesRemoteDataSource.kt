package com.haidoan.android.stren.core.datasource

import com.google.firebase.firestore.Query
import com.haidoan.android.stren.core.repository.DEFAULT_ITEM_COUNT_LIMIT

interface ExercisesRemoteDataSource {
    /**
     * This method returns a [Query] which can be reused to build different queries instead
     * of just running the query immediately
     */
    suspend fun getExercisesWithLimitAsQuery(limit: Long = DEFAULT_ITEM_COUNT_LIMIT): Query
}