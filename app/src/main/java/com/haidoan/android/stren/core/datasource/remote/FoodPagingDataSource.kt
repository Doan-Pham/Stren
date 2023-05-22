package com.haidoan.android.stren.core.datasource.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.haidoan.android.stren.core.model.Food
import com.haidoan.android.stren.core.network.model.asExternalModel
import com.haidoan.android.stren.core.repository.DEFAULT_FOOD_DATA_PAGE_SIZE
import okio.IOException
import retrofit2.HttpException
import timber.log.Timber

class FoodPagingDataSource(
    private val pageSize: Int = DEFAULT_FOOD_DATA_PAGE_SIZE,
    private val foodRemoteDataSource: FoodRemoteDataSource,
    private val foodNameToQuery: String
) : PagingSource<Int, Food>() {
    override fun getRefreshKey(state: PagingState<Int, Food>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, Food> {
        try {
            // Start refresh at page 1 if undefined.
            val nextPageNumber = params.key ?: 1
            Timber.d("nextPageNumber:$nextPageNumber ")


            val response =
                if (foodNameToQuery.isEmpty() || foodNameToQuery.isBlank()) {
                    foodRemoteDataSource.getPagedFoodData(
                        dataPageIndex = nextPageNumber
                    )
                } else {
                    foodRemoteDataSource.getPagedFoodDataByName(
                        foodName = foodNameToQuery,
                        dataPageIndex = nextPageNumber
                    )
                }
            Timber.d("response - size:${response.size}")

            return LoadResult.Page(
                data = response.map { it.asExternalModel() },
                prevKey = null,
                nextKey = if (response.size < pageSize) null else nextPageNumber + 1
            )
        } catch (e: IOException) {
            Timber.e("Load - Exception: $e ")
            return LoadResult.Error(e)
        } catch (e: HttpException) {
            Timber.e("Load - Exception: $e ")
            return LoadResult.Error(e)
        }
    }
}