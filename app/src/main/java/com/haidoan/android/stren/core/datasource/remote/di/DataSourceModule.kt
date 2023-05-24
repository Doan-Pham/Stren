package com.haidoan.android.stren.core.datasource.remote.di

import com.haidoan.android.stren.core.datasource.*
import com.haidoan.android.stren.core.datasource.remote.*
import com.haidoan.android.stren.core.datasource.remote.base.*
import com.haidoan.android.stren.core.datasource.remote.impl.EatingDayFirestoreDataSource
import com.haidoan.android.stren.core.datasource.remote.impl.ExercisesFirestoreDataSource
import com.haidoan.android.stren.core.datasource.remote.impl.RoutinesFirestoreDataSource
import com.haidoan.android.stren.core.datasource.remote.impl.WorkoutFirestoreDataSource
import com.haidoan.android.stren.core.datasource.remote.model.FoodRetrofitDataSource
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class DataSourceModule {
    @Binds
    abstract fun bindRemoteExercisesDataSource(impl: ExercisesFirestoreDataSource): ExercisesRemoteDataSource

    @Binds
    abstract fun bindRemoteWorkoutsDataSource(impl: WorkoutFirestoreDataSource): WorkoutRemoteDataSource

    @Binds
    abstract fun bindRemoteRoutinesDataSource(impl: RoutinesFirestoreDataSource): RoutinesRemoteDataSource

    @Binds
    abstract fun bindRemoteFoodDataSource(impl: FoodRetrofitDataSource): FoodRemoteDataSource

    @Binds
    abstract fun bindEatingDayRemoteDataSource(impl: EatingDayFirestoreDataSource): EatingDayRemoteDataSource
}