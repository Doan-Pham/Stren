package com.haidoan.android.stren.feat.nutrition.diary

import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.model.FoodNutrient
import java.time.LocalDate

internal sealed interface NutritionDiaryUiState {
    object Loading : NutritionDiaryUiState
    data class LoadComplete(
        val userId: String,
        val eatingDay: EatingDay,
        val selectedDate: LocalDate,
        val datesTracked: List<LocalDate>,
        val goalCalories: Float,
        val goalsByMacronutrient: Map<FoodNutrient, Float>
    ) : NutritionDiaryUiState
}