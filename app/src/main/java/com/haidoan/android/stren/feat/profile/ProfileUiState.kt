package com.haidoan.android.stren.feat.profile

import com.haidoan.android.stren.core.model.User

internal sealed interface ProfileUiState {
    object Loading : ProfileUiState
    data class LoadComplete(
        val currentUser: User
    ) : ProfileUiState
}