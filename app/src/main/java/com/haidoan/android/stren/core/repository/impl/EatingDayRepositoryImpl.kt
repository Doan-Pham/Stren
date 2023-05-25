package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.base.EatingDayRemoteDataSource
import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.model.Meal
import com.haidoan.android.stren.core.repository.base.EatingDayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class EatingDayRepositoryImpl @Inject constructor(private val eatingDayRemoteDataSource: EatingDayRemoteDataSource) :
    EatingDayRepository {

    override fun getEatingDayByUserIdAndDate(userId: String, date: LocalDate): Flow<EatingDay> =
        eatingDayRemoteDataSource.getEatingDayStream(userId, date).catch {
            Timber.e("getEatingDayByUserIdAndDate() - Exception: ${it.message}")
        }

    override suspend fun getEatingDayById(userId: String, eatingDayId: String): EatingDay {
        return try {
            eatingDayRemoteDataSource.getEatingDayById(userId, eatingDayId)
        } catch (exception: Exception) {
            Timber.e("getEatingDayById() - Exception: ${exception.message}")
            EatingDay.undefined
        }
    }

    override suspend fun updateEatingDay(userId: String, eatingDayId: String, meals: List<Meal>) {
        try {
            Timber.d("updateWorkout() - userId: $userId; eatingDayId: $eatingDayId")
            eatingDayRemoteDataSource.updateEatingDay(userId, eatingDayId, meals)
        } catch (exception: Exception) {
            Timber.e("updateWorkout() - Exception: ${exception.message}")
        }
    }
}