package com.haidoan.android.stren.core.repository

import com.haidoan.android.stren.core.datasource.remote.FoodRemoteDataSource
import com.haidoan.android.stren.core.model.Food
import com.haidoan.android.stren.core.network.model.asExternalModel
import timber.log.Timber
import javax.inject.Inject

class FoodRepositoryImpl @Inject constructor(private val foodRemoteDataSource: FoodRemoteDataSource) :
    FoodRepository {
    override suspend fun getAllFood(): List<Food> {
        return try {
            Timber.d("getAllFood() called")
            foodRemoteDataSource.getAllFood().map { it.asExternalModel() }
        } catch (exception: Exception) {
            Timber.e("getAllFood() - Exception: ${exception.message}")
            emptyList()
        }
    }
}