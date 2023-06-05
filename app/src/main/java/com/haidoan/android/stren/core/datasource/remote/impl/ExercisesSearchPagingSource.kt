package com.haidoan.android.stren.core.datasource.remote.impl

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haidoan.android.stren.core.datasource.remote.base.ExercisesSearchDataSource
import com.haidoan.android.stren.core.model.Exercise
import com.haidoan.android.stren.core.model.ExerciseQueryParameters
import com.haidoan.android.stren.core.repository.base.DEFAULT_EXERCISE_DATA_PAGE_SIZE
import timber.log.Timber
import javax.inject.Inject

class ExercisesSearchPagingSource @Inject constructor(
    private val pageSize: Long = DEFAULT_EXERCISE_DATA_PAGE_SIZE,
    private val exercisesSearchDataSource: ExercisesSearchDataSource,
    private val exerciseQueryParameters: ExerciseQueryParameters
) :
    PagingSource<Int, Exercise>() {
    override fun getRefreshKey(state: PagingState<Int, Exercise>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Exercise> {
        return try {
            val nextPageNumber = params.key ?: 0
            val response = exercisesSearchDataSource.searchExercise(
                exerciseQueryParameters = exerciseQueryParameters,
                dataPageSize = pageSize,
                dataPageIndex = nextPageNumber
            )
            LoadResult.Page(
                data = response,
                prevKey = null, // Only paging forward.
                nextKey = if (response.size < pageSize) null else nextPageNumber + 1
            )
        } catch (e: Exception) {
            Timber.e("ExercisesSearchPagingSource-load() fails with exception: $e")
            LoadResult.Error(e)
        }
    }
}