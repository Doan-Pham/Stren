package com.haidoan.android.stren.core.repository

import com.haidoan.android.stren.core.model.Workout
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface WorkoutsRepository {
    fun getWorkoutsByUserIdAndDate(userId: String, date: LocalDate): Flow<List<Workout>>
}