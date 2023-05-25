package com.haidoan.android.stren.core.datasource.remote.impl

import com.haidoan.android.stren.BuildConfig
import com.haidoan.android.stren.core.datasource.remote.base.FoodRemoteDataSource
import com.haidoan.android.stren.core.datasource.remote.di.IoDispatcher
import com.haidoan.android.stren.core.datasource.remote.model.NetworkFood
import com.haidoan.android.stren.core.datasource.remote.model.NetworkFoodNutrient
import com.jakewharton.retrofit2.converter.kotlinx.serialization.asConverterFactory
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.MediaType.Companion.toMediaType
import retrofit2.Retrofit
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * In the FDC API, each nutrient is defined by a "number" and since we don't need all nutrients
 * (Ex: those that are only relevant for research), this val's keySet represents
 * only the needed nutrients for personal and commercial use (The nutrientName
 * is simply for better understanding, since having only numbers is confusing)
 */
internal val relevantNutrientNameByNumber = mapOf(
    208 to "Energy",
    203 to "Protein",
    205 to "Carbohydrate, by difference",
    204 to "Total lipid (fat)",
    291 to "Fiber, total dietary",
    320 to "Vitamin A, RAE",
    255 to "Water",
    415 to "Vitamin B-6",
    418 to "Vitamin B-12",
    401 to "Vitamin C, total ascorbic acid",
    328 to "Vitamin D (D2 + D3)",
    323 to "Vitamin E (alpha-tocopherol)",
    430 to "Vitamin K (phylloquinone)",
    301 to "Calcium, Ca",
    303 to "Iron, Fe",
    307 to "Sodium, Na",
    306 to "Potassium, K",
    305 to "Phosphorus, P",
    304 to "Magnesium, Mg",
    309 to "Zinc, Zn",
    269 to "Sugars, total including NLEA",
    601 to "Cholesterol",
    262 to "Caffeine",
    221 to "Alcohol"
)

/**
 * There are different types of foods present in the FDC API, among them are the "Experimental"
 * and "Foundation" foods which are grown and harvested in a controlled environment mainly for
 * research, and not fit for commercial use. This list helps excludes those types of data
 */
private val defaultDataTypes = listOf(
    "Branded", "Survey (FNDDS)", "SR Legacy"
)

/**
 * Retrofit API declaration for FDC (Food Data Central) API
 */
private interface RetrofitFoodApi {
    @GET(value = "foods/list")
    suspend fun getAllFood(
        @Query("dataType") dataType: List<String> = defaultDataTypes,
        @Query("pageSize") pageSize: Int?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("api_key") api_key: String? = BuildConfig.FDC_API_KEY
    ): List<NetworkFood<NetworkFoodNutrient.DefaultNetworkFoodNutrient>>

    @GET(value = "foods/search")
    suspend fun getAllFoodByName(
        @Query("query") query: String?,
        @Query("dataType") dataType: List<String> = defaultDataTypes,
        @Query("pageSize") pageSize: Int?,
        @Query("pageNumber") pageNumber: Int?,
        @Query("api_key") api_key: String? = BuildConfig.FDC_API_KEY
    ): NetworkFoodSearchResponse

    @GET(value = "food/{fdcId}")
    suspend fun getFoodById(
        @Path("fdcId") id: String?,
        /**
         * If "format" is "full", Certain types of food won't include the "unitName" for some reason (Not specified in the API Spec !!!!????)
         */
        @Query("format") format: String? = "abridged",
        /**
         * This query only works for all foods if the "format" is "abridged". If "format" is "full",
         * it'll only work for certain types of foods
         */
        @Query("nutrients") nutrients: List<Int>? = relevantNutrientNameByNumber.keys.toList(),
        @Query("api_key") api_key: String? = BuildConfig.FDC_API_KEY
    ): NetworkFood<NetworkFoodNutrient.DefaultNetworkFoodNutrient>
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

@Serializable
private data class NetworkFoodSearchResponse(
    val totalHits: Int,
    val currentPage: Int,
    @SerialName("foods")
    val foods: List<NetworkFood<NetworkFoodNutrient.SearchResultNetworkFoodNutrient>>
)

/**
 * [Retrofit] backend [FoodRemoteDataSource]
 */
@Singleton
class FoodRetrofitDataSource @Inject constructor(
    @IoDispatcher private val ioDispatcher: CoroutineDispatcher,
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
    ): List<NetworkFood<NetworkFoodNutrient.DefaultNetworkFoodNutrient>> =
        networkApi.getAllFood(pageSize = dataPageSize, pageNumber = dataPageIndex)

    override suspend fun getPagedFoodDataByName(
        dataPageSize: Int,
        dataPageIndex: Int,
        foodName: String
    ): List<NetworkFood<NetworkFoodNutrient.SearchResultNetworkFoodNutrient>> =
        withContext(ioDispatcher) {
            val response = networkApi.getAllFoodByName(
                query = foodName,
                pageSize = dataPageSize,
                pageNumber = dataPageIndex,
            )
            Timber.d("food: $response")
            response.foods
        }


    override suspend fun getFoodById(id: String): NetworkFood<NetworkFoodNutrient.DefaultNetworkFoodNutrient> =
        networkApi.getFoodById(id = id)
}