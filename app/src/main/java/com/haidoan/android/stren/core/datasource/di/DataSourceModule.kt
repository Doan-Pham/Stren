package com.haidoan.android.stren.core.datasource.di

import com.haidoan.android.stren.core.datasource.*
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
}