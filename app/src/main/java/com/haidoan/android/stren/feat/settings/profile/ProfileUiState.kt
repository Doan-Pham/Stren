package com.haidoan.android.stren.feat.settings.profile

import com.haidoan.android.stren.core.model.ActivityLevel
import com.haidoan.android.stren.core.model.Sex
import com.haidoan.android.stren.core.model.User
import com.haidoan.android.stren.core.model.WeightGoal

internal sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class LoadComplete(
        val currentUser: User,
        val sexes: List<Sex> = Sex.values().toList(),
        val activityLevels: List<ActivityLevel> = ActivityLevel.values().toList(),
        val weightGoals: List<WeightGoal> = WeightGoal.values().toList(),
    ) : ProfileUiState
}