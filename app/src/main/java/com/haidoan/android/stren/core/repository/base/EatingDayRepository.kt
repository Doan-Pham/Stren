package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.model.Meal
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EatingDayRepository {
    fun getEatingDayByUserIdAndDate(userId: String, date: LocalDate): Flow<EatingDay>

    suspend fun getEatingDayById(userId: String, eatingDayId: String): EatingDay

    suspend fun updateEatingDay(userId: String, eatingDayId: String, meals: List<Meal>)
    suspend fun getEatingDayByDate(userId: String, selectedDate: LocalDate): EatingDay
    suspend fun addEatingDay(userId: String, eatingDay: EatingDay): String
}