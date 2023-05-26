package com.haidoan.android.stren.feat.nutrition.diary.add_food

import com.haidoan.android.stren.core.model.Food

internal sealed interface AddEditFoodEntryUiState {
    object Loading : AddEditFoodEntryUiState
    data class LoadComplete(val food: Food) : AddEditFoodEntryUiState
}