package com.haidoan.android.stren.core.datasource.remote.base

import com.haidoan.android.stren.core.model.TrackedCategory

interface DefaultValuesRemoteDataSource {
    suspend fun getDefaultTrackedCategories(): List<TrackedCategory>
}