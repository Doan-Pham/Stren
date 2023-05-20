package com.haidoan.android.stren.feat.nutrition.food.detail

import com.haidoan.android.stren.core.model.Food

internal sealed interface FoodDetailUiState {
    object Loading : FoodDetailUiState
    data class LoadComplete(val food: Food) : FoodDetailUiState
}