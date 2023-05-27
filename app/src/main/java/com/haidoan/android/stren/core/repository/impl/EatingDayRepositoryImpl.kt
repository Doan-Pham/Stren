package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.base.EatingDayRemoteDataSource
import com.haidoan.android.stren.core.model.CaloriesOfDate
import com.haidoan.android.stren.core.model.EatingDay
import com.haidoan.android.stren.core.repository.base.EatingDayRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import timber.log.Timber
import java.time.LocalDate
import javax.inject.Inject

class EatingDayRepositoryImpl @Inject constructor(private val eatingDayRemoteDataSource: EatingDayRemoteDataSource) :
    EatingDayRepository {

    override suspend fun getEatingDayStream(userId: String, date: LocalDate): Flow<EatingDay> =
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

    override suspend fun updateEatingDay(userId: String, eatingDay: EatingDay) {
        try {
            Timber.d("updateWorkout() - userId: $userId; eatingDayId: ${eatingDay.id}")
            eatingDayRemoteDataSource.updateEatingDay(userId, eatingDay)
        } catch (exception: Exception) {
            Timber.e("updateWorkout() - Exception: ${exception.message}")
        }
    }

    override suspend fun getEatingDayByDate(userId: String, selectedDate: LocalDate): EatingDay {
        return try {
            eatingDayRemoteDataSource.getEatingDayByDate(userId, selectedDate)
        } catch (exception: Exception) {
            Timber.e("getEatingDayByDate() - Exception: ${exception.message}")
            EatingDay.undefined
        }
    }

    override suspend fun getCaloriesOfDatesStream(
        userId: String,
        startDate: LocalDate,
        endDate: LocalDate
    ): Flow<List<CaloriesOfDate>> =
        eatingDayRemoteDataSource.getCaloriesOfDatesStream(userId, startDate, endDate).catch {
            Timber.e("getEatingDayBetweenDates() - Exception: ${it.message}")
        }


    override suspend fun addEatingDay(userId: String, eatingDay: EatingDay): String {
        return try {
            eatingDayRemoteDataSource.addEatingDay(userId, eatingDay)
        } catch (exception: Exception) {
            Timber.e("addEatingDay() - Exception: ${exception.message}")
            "Undefined Doc ID"
        }
    }

    override suspend fun getDatesUserTracked(userId: String): Flow<List<LocalDate>> =
        flow {
            Timber.d("getDatesUserTracked() has been called - userId: $userId")
            emit(eatingDayRemoteDataSource.getDatesUserTracked(userId))
        }.catch {
            Timber.e("getDatesThatHaveWorkoutByUserId() - Exception: ${it.message}")
        }
}