package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.Biometrics
import com.haidoan.android.stren.core.model.TrackedCategory

interface DefaultValuesRepository {
    suspend fun getDefaultTrackedCategories(): List<TrackedCategory>

    suspend fun getDefaultBiometrics(): List<Biometrics>
}