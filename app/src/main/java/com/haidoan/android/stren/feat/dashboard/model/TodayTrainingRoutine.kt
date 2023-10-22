package com.haidoan.android.stren.feat.dashboard.model

import com.haidoan.android.stren.core.model.Routine

internal data class TodayTrainingProgram(
    val programId: String,
    val programName: String,
    val weekIndex: Int,
    val weeklyDayOffset: Int,
    val routines: List<Routine>,
)
