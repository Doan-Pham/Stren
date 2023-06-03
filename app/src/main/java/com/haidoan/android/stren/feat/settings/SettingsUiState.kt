package com.haidoan.android.stren.feat.settings

import com.haidoan.android.stren.core.model.User

internal sealed interface SettingsUiState {
    object Loading : SettingsUiState
    data class LoadComplete(
        val currentUser: User
    ) : SettingsUiState
}