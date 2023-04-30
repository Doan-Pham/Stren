package com.haidoan.android.stren.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import com.haidoan.android.stren.core.datasource.ExercisesRemoteDataSource
import javax.inject.Inject

class ExercisesRepositoryImpl @Inject constructor(
    private val dataSource: ExercisesRemoteDataSource
) : ExercisesRepository {

    override fun getExercisesWithLimit(limit: Long) = Pager(
        config = PagingConfig(
            pageSize = limit.toInt(),
        ),
        pagingSourceFactory = { ExercisesPagingSource(dataSource, limit) }
    ).flow

}