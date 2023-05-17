package com.haidoan.android.stren.core.repository

import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import com.haidoan.android.stren.core.datasource.remote.FoodPagingDataSource
import com.haidoan.android.stren.core.datasource.remote.FoodRemoteDataSource
import com.haidoan.android.stren.core.model.Food
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(private val foodRemoteDataSource: FoodRemoteDataSource) :
    FoodRepository {
    override fun getPagedFoodData(pageSize: Int): Flow<PagingData<Food>> =
        Pager(config = PagingConfig(
            pageSize = pageSize,
        ), pagingSourceFactory = {
            FoodPagingDataSource(
                pageSize = pageSize,
                foodRemoteDataSource = foodRemoteDataSource
            )
        }).flow
}