package com.haidoan.android.stren.core.repository.base

import com.haidoan.android.stren.core.model.EatingDay
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EatingDayRepository {
    fun getEatingDayByUserIdAndDate(userId: String, date: LocalDate): Flow<EatingDay>
}