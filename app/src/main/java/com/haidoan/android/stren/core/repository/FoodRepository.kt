package com.haidoan.android.stren.core.repository

import com.haidoan.android.stren.core.model.Food

interface FoodRepository {
    suspend fun getAllFood(): List<Food>
}