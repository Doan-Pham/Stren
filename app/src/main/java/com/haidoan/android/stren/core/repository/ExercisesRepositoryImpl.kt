package com.haidoan.android.stren.core.repository

import android.util.Log
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haidoan.android.stren.core.datasource.ExercisesRemoteDataSource
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.MuscleGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

private const val TAG = "ExercisesRepositoryImpl"

class ExercisesRepositoryImpl @Inject constructor(
    private val dataSource: ExercisesRemoteDataSource
) : ExercisesRepository {

    override fun getExercisesWithLimit(limit: Long) = Pager(
        config = PagingConfig(
            pageSize = limit.toInt(),
        ),
        pagingSourceFactory = { ExercisesPagingSource(dataSource.getExercisesWithLimitAsQuery(limit)) }
    ).flow

    override fun getExercisesByNameWithLimit(
        exerciseName: String,
        limit: Long
    ): Flow<PagingData<Exercise>> = Pager(
        config = PagingConfig(
            pageSize = limit.toInt(),
        ),
        pagingSourceFactory = {
            ExercisesPagingSource(
                dataSource.getExercisesByNameAsQuery(
                    exerciseName,
                    limit
                )
            )
        }
    ).flow

    override fun getAllExerciseCategories(): Flow<List<ExerciseCategory>> = flow {
        emit(dataSource.getAllExerciseCategories())
    }
        .catch {
            Log.e(
                TAG,
                "getAllExerciseCategories() - Exception: ${it.message}"
            )
        }

    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> {
        TODO("Not yet implemented")
    }

}