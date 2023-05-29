package com.haidoan.android.stren.core.repository.impl

import com.haidoan.android.stren.core.datasource.remote.base.DefaultValuesRemoteDataSource
import com.haidoan.android.stren.core.model.TrackedCategory
import com.haidoan.android.stren.core.repository.base.DefaultValuesRepository
import timber.log.Timber
import javax.inject.Inject

class DefaultValuesRepositoryImpl @Inject constructor(private val dataSource: DefaultValuesRemoteDataSource) :
    DefaultValuesRepository {
    override suspend fun getDefaultTrackedCategories(): List<TrackedCategory> {
        return try {
            dataSource.getDefaultTrackedCategories()
        } catch (ex: Exception) {
            Timber.e("getDefaultTrackedCategories() - exception: $ex")
            listOf()
        }
    }
}