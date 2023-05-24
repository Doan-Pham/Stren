package com.haidoan.android.stren.core.datasource.remote.base

import com.haidoan.android.stren.core.model.EatingDay
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate

interface EatingDayRemoteDataSource {
    fun getEatingDayStream(userId: String, date: LocalDate): Flow<EatingDay>
}