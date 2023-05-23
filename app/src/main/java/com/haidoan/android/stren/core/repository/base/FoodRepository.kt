package com.haidoan.android.stren.core.repository.base

import androidx.paging.PagingData
import com.haidoan.android.stren.core.model.Food
import kotlinx.coroutines.flow.Flow

const val DEFAULT_FOOD_DATA_PAGE_SIZE = 20

interface FoodRepository {
    fun getPagedFoodData(
        pageSize: Int = DEFAULT_FOOD_DATA_PAGE_SIZE,
        foodNameToQuery: String
    ): Flow<PagingData<Food>>

    suspend fun getFoodById(id: String): Food
}