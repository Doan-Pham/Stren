package com.haidoan.android.stren.core.datasource.remote.di

import com.algolia.search.client.ClientSearch
import com.algolia.search.client.Index
import com.algolia.search.logging.LogLevel
import com.algolia.search.model.APIKey
import com.algolia.search.model.ApplicationID
import com.algolia.search.model.IndexName
import com.haidoan.android.stren.BuildConfig
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.serialization.json.Json
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    @Provides
    @Singleton
    fun providesNetworkJson(): Json = Json {
        ignoreUnknownKeys = true
    }

    @Provides
    @Singleton
    fun okHttpCallFactory(): Call.Factory = OkHttpClient.Builder()
        .connectTimeout(1, TimeUnit.MINUTES)
        .readTimeout(1, TimeUnit.MINUTES)
        .addInterceptor(
            HttpLoggingInterceptor()
                .apply {
                    if (BuildConfig.DEBUG) {
                        level = HttpLoggingInterceptor.Level.BODY
                    }
                },
        )
        .build()

    @IoDispatcher
    @Provides
    fun providesIoDispatcher(): CoroutineDispatcher = Dispatchers.IO

    @ExerciseIndex
    @Provides
    @Singleton
    fun providesAlgoliaIndex(): Index {
        val client = ClientSearch(
            applicationID = ApplicationID(BuildConfig.ALGOLIA_APPLICATION_ID),
            apiKey = APIKey(BuildConfig.ALGOLIA_SEARCH_API_KEY),
            logLevel = LogLevel.All
        )
        val indexName = IndexName("Exercise")
        return client.initIndex(indexName)
    }
}

@Qualifier
annotation class IoDispatcher

@Qualifier
annotation class ExerciseIndex