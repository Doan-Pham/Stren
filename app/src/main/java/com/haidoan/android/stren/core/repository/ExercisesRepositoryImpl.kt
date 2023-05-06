package com.haidoan.android.stren.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haidoan.android.stren.core.datasource.ExercisesRemoteDataSource
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseFilterStandards
import com.haidoan.android.stren.core.model.MuscleGroup
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

private const val TAG = "ExercisesRepositoryImpl"

class ExercisesRepositoryImpl @Inject constructor(
    private val dataSource: ExercisesRemoteDataSource
) : ExercisesRepository {

    override fun getExercisesWithLimit(limit: Long) = Pager(
        config = PagingConfig(
            pageSize = limit.toInt(),
        ),
        pagingSourceFactory = {
            ExercisesPagingSource(
                dataSource.getExercisesWithLimitAsQuery(limit).toQueryWrapper()
            )
        }
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
                ).toQueryWrapper()
            )
        }
    ).flow

    override fun getAllExerciseCategories(): Flow<List<ExerciseCategory>> = flow {
        Timber.d(
            TAG,
            "getAllExerciseCategories() has been called"
        )
        emit(dataSource.getAllExerciseCategories())
    }
        .catch {
            Timber.e(
                TAG,
                "getAllExerciseCategories() - Exception: ${it.message}"
            )
        }

    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> = flow {
        Timber.d(
            TAG,
            "getAllMuscleGroups() has been called"
        )
        emit(dataSource.getAllMuscleGroups())
    }
        .catch {
            Timber.e(
                TAG,
                "getAllMuscleGroups() - Exception: ${it.message}"
            )
        }

    override fun filterExercises(
        filterStandards: ExerciseFilterStandards,
        resultCountLimit: Long
    ): Flow<PagingData<Exercise>> = Pager(
        config = PagingConfig(
            pageSize = resultCountLimit.toInt(),
        ),
        pagingSourceFactory = {
            ExercisesPagingSource(
                dataSource.getFilteredExercisesAsQuery(
                    filterStandards,
                    resultCountLimit
                )
            )
        }
    ).flow

}