package com.haidoan.android.stren.core.repository.impl

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haidoan.android.stren.core.datasource.remote.base.ExercisesRemoteDataSource
import com.haidoan.android.stren.core.datasource.remote.base.ExercisesSearchDataSource
import com.haidoan.android.stren.core.datasource.remote.di.AlgoliaDataSource
import com.haidoan.android.stren.core.datasource.remote.impl.ExercisesSearchPagingSource
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseCategory
import com.haidoan.android.stren.core.model.ExerciseQueryParameters
import com.haidoan.android.stren.core.model.MuscleGroup
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val dataSource: ExercisesRemoteDataSource,
    @AlgoliaDataSource
    private val searchDataSource: ExercisesSearchDataSource
) : ExercisesRepository {

    override fun getExercisesWithLimit(limit: Long) = Pager(config = PagingConfig(
        pageSize = limit.toInt(),
    ), pagingSourceFactory = {
        ExercisesPagingSource(
            dataSource.getExercisesWithLimitAsQuery(limit).toQueryWrapper()
        )
    }).flow

    override fun getExercisesByNameWithLimit(
        exerciseName: String, limit: Long
    ): Flow<PagingData<Exercise>> = Pager(config = PagingConfig(
        pageSize = limit.toInt(),
    ), pagingSourceFactory = {
        ExercisesPagingSource(
            dataSource.getExercisesByNameAsQuery(
                exerciseName, limit
            ).toQueryWrapper()
        )
    }).flow

    override fun getAllExerciseCategories(): Flow<List<ExerciseCategory>> = flow {
        Timber.d("getAllExerciseCategories() has been called")
        emit(dataSource.getAllExerciseCategories())
    }.catch {
        Timber.e("getAllExerciseCategories() - Exception: ${it.message}")
    }

    override fun getAllMuscleGroups(): Flow<List<MuscleGroup>> = flow {
        Timber.d("getAllMuscleGroups() has been called")
        emit(dataSource.getAllMuscleGroups())
    }.catch {
        Timber.e("getAllMuscleGroups() - Exception: ${it.message}")
    }

    override fun searchExercises(
        filterStandards: ExerciseQueryParameters,
        resultCountLimit: Long
    ): Flow<PagingData<Exercise>> = Pager(config = PagingConfig(
        pageSize = resultCountLimit.toInt(),
    ), pagingSourceFactory = {
        ExercisesSearchPagingSource(
            exercisesSearchDataSource = searchDataSource,
            exerciseQueryParameters = filterStandards,
            pageSize = resultCountLimit
        )
    }).flow

    override fun getExerciseById(exerciseId: String): Flow<Exercise> = flow {
        Timber.d("getExerciseById() has been called - exerciseId: $exerciseId")
        emit(dataSource.getExerciseById(exerciseId))
    }.catch {
        Timber.e("getExerciseById() - Exception: ${it.message}")
    }

    override suspend fun getExercisesByIds(exerciseIds: List<String>): List<Exercise> =
        dataSource.getExercisesByIds(exerciseIds)

    override suspend fun createCustomExercise(userId: String, exercise: Exercise) = try {
        dataSource.createCustomExercise(userId, exercise)
    } catch (ex: Exception) {
        Timber.e("createCustomExercise() - Exception: $ex, ${ex.printStackTrace()}")
    }
}