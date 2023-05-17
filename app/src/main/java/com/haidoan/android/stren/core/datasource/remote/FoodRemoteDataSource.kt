package com.haidoan.android.stren.core.datasource.remote

import com.haidoan.android.stren.core.network.model.NetworkFood

interface FoodRemoteDataSource {

    suspend fun getAllFood(): List<NetworkFood>
}