package com.haidoan.android.stren.core.repository.di

import android.nfc.tech.MifareUltralight.PAGE_SIZE
import androidx.paging.PagingConfig
import com.google.firebase.firestore.FirebaseFirestoreSettings
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.haidoan.android.stren.core.repository.EXERCISE_COLLECTION_PATH
import com.haidoan.android.stren.core.repository.FirestorePagingSource
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

const val PAGE_SIZE = 30

@Module
@InstallIn(SingletonComponent::class)
internal object DataSourceModule {
    @Provides
    fun provideExercisesQuery(): Query {
        val firestore = Firebase.firestore
        firestore.firestoreSettings =
            FirebaseFirestoreSettings.Builder().setPersistenceEnabled(false).build()
        return firestore.collection(EXERCISE_COLLECTION_PATH)
            .orderBy("name", Query.Direction.ASCENDING)
            .limit(PAGE_SIZE.toLong())
    }


    @Provides
    fun provideFirestorePagingSource(exercisesQuery: Query) = FirestorePagingSource(exercisesQuery)

    @Provides
    fun providePagingConfig() = PagingConfig(
        pageSize = PAGE_SIZE
    )
}