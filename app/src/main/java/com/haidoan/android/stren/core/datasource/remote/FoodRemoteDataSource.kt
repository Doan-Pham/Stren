package com.haidoan.android.stren.core.datasource.remote

import com.haidoan.android.stren.core.network.model.NetworkFood
import com.haidoan.android.stren.core.network.model.NetworkFoodNutrient
import com.haidoan.android.stren.core.repository.DEFAULT_FOOD_DATA_PAGE_SIZE

interface FoodRemoteDataSource {
    suspend fun getPagedFoodData(
        dataPageSize: Int = DEFAULT_FOOD_DATA_PAGE_SIZE,
        dataPageIndex: Int = 1
    ): List<NetworkFood<NetworkFoodNutrient.DefaultNetworkFoodNutrient>>

    suspend fun getPagedFoodDataByName(
        dataPageSize: Int = DEFAULT_FOOD_DATA_PAGE_SIZE,
        dataPageIndex: Int = 1,
        foodName: String
    ): List<NetworkFood<NetworkFoodNutrient.SearchResultNetworkFoodNutrient>>

    suspend fun getFoodById(id: String): NetworkFood<NetworkFoodNutrient.DefaultNetworkFoodNutrient>
}