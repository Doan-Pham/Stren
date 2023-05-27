package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.CaloriesOfDate
import com.haidoan.android.stren.core.model.EatingDay
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EatingDayRepository {
    suspend fun getEatingDayStream(userId: String, date: LocalDate): Flow<EatingDay>

    suspend fun getEatingDayById(userId: String, eatingDayId: String): EatingDay

    suspend fun getEatingDayByDate(userId: String, selectedDate: LocalDate): EatingDay
    suspend fun addEatingDay(userId: String, eatingDay: EatingDay): String
    suspend fun getDatesUserTracked(userId: String): Flow<List<LocalDate>>
    suspend fun updateEatingDay(userId: String, eatingDay: EatingDay)
    suspend fun getCaloriesOfDatesStream(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CaloriesOfDate>>
}