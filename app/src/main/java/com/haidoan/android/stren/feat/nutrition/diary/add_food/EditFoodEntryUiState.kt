package com.haidoan.android.stren.feat.nutrition.diary.add_food

import com.haidoan.android.stren.core.model.Food

internal sealed interface EditFoodEntryUiState {
    object Loading : EditFoodEntryUiState
    data class LoadComplete(val food: Food) : EditFoodEntryUiState
}