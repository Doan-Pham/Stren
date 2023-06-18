package com.haidoan.android.stren.feat.settings.measurements

import com.haidoan.android.stren.core.model.BiometricsRecord

internal sealed interface MeasurementsUiState {
    object Loading : MeasurementsUiState
    data class LoadComplete(
        val biometricsRecords: List<BiometricsRecord>
    ) : MeasurementsUiState
}