package com.haidoan.android.stren.core.repository

import androidx.paging.PagingData
import com.haidoan.android.stren.core.model.Food
import kotlinx.coroutines.flow.Flow

const val DEFAULT_FOOD_DATA_PAGE_SIZE = 20

interface FoodRepository {
    fun getPagedFoodData(pageSize: Int = DEFAULT_FOOD_DATA_PAGE_SIZE): Flow<PagingData<Food>>
}