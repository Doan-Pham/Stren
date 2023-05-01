package com.haidoan.android.stren.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haidoan.android.stren.core.datasource.ExercisesRemoteDataSource
import com.haidoan.android.stren.core.model.Exercise
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

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

}