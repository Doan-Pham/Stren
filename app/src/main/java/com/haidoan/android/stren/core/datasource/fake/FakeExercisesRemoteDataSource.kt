package com.haidoan.android.stren.core.datasource.fake

import com.google.firebase.firestore.Query
import com.haidoan.android.stren.core.datasource.ExercisesRemoteDataSource
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseFilterStandards
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.QueryWrapper
import com.haidoan.android.stren.core.testing.data.EXERCISE_CATEGORIES_TEST_DATA

class FakeExercisesRemoteDataSource : ExercisesRemoteDataSource {
    private var _exerciseCategories = EXERCISE_CATEGORIES_TEST_DATA

    fun setExerciseCategories(categories: List<ExerciseCategory>) {
        _exerciseCategories = categories
    }

    override fun getExercisesWithLimitAsQuery(limit: Long): Query {
        TODO("Not yet implemented")
    }

    override fun getExercisesByNameAsQuery(exerciseName: String, resultCountLimit: Long): Query {
        TODO("Not yet implemented")
    }

    override suspend fun getAllExerciseCategories(): List<ExerciseCategory> = _exerciseCategories

    override suspend fun getAllMuscleGroups(): List<MuscleGroup> {
        TODO("Not yet implemented")
    }

    override fun getFilteredExercisesAsQuery(
        filterStandards: ExerciseFilterStandards,
        resultCountLimit: Long
    ): QueryWrapper {
        TODO("Not yet implemented")
    }
}