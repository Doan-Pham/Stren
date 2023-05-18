package com.haidoan.android.stren.core.network

import com.haidoan.android.stren.BuildConfig
import com.haidoan.android.stren.core.datasource.remote.FoodRemoteDataSource
import com.haidoan.android.stren.core.network.model.NetworkFood
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import javax.inject.Inject
import javax.inject.Singleton


/**
 * Retrofit API declaration for FDC (Food Data Central) API
 */
private interface RetrofitFoodApi {
    @GET(value = "foods/list")
    suspend fun getAllFood(
        @Query("sortBy") sortBy: String? = "lowercaseDescription.keyword",
        @Query("pageSize") pageSize: Int?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("sortOrder") sortOrder: String? = "asc",
        @Query("api_key") api_key: String? = BuildConfig.FDC_API_KEY
    ): List<NetworkFood>

    @GET(value = "food/{fdcId}")
    suspend fun getFoodById(
        @Path("fdcId") id: String?,
        @Query("format") format: String? = "abridged",
        @Query("api_key") api_key: String? = BuildConfig.FDC_API_KEY
    ): NetworkFood
}

private const val FdaFoodBaseUrl = BuildConfig.FDC_API_BASE_URL

/**
 * Wrapper for data provided from the [FdaFoodBaseUrl]
 * This is for the case where the result json starts with '{', if your result json
 * starts with '[', simply convert to List of Model class
 */
@Serializable
private data class NetworkResponse<T>(
    val data: T,
)

/**
 * [Retrofit] backend [FoodRemoteDataSource]
 */
@Singleton
class FoodRetrofitDataSource @Inject constructor(
    networkJson: Json,
    okhttpCallFactory: Call.Factory,
) : FoodRemoteDataSource {

    private val networkApi = Retrofit.Builder().baseUrl(FdaFoodBaseUrl)
        .callFactory(okhttpCallFactory)
        .addConverterFactory(
            networkJson.asConverterFactory("application/json".toMediaType()),
        )
        .build()
        .create(RetrofitFoodApi::class.java)

    override suspend fun getPagedFoodData(
        dataPageSize: Int,
        dataPageIndex: Int
    ): List<NetworkFood> =
        networkApi.getAllFood(pageSize = dataPageSize, pageNumber = dataPageIndex)

    override suspend fun getFoodById(id: String): NetworkFood = networkApi.getFoodById(id = id)
}
