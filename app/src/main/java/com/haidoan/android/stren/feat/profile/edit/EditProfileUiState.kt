package com.haidoan.android.stren.feat.profile.edit

import com.haidoan.android.stren.core.domain.NutritionCalculationUseCase
import com.haidoan.android.stren.core.model.User

internal sealed interface EditProfileUiState {
    object Loading : EditProfileUiState
    data class LoadComplete(
        val currentUser: User,
        val sexes: List<NutritionCalculationUseCase.Sex> = NutritionCalculationUseCase.Sex.values()
            .toList(),
    ) : EditProfileUiState
}