package com.haidoan.android.stren.feat.settings.profile

import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase
import com.haidoan.android.stren.core.model.User

internal sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class LoadComplete(
        val currentUser: User,
        val sexes: List<NutritionCalculationUseCase.Sex> = NutritionCalculationUseCase.Sex.values()
            .toList(),
    ) : ProfileUiState
}