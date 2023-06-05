package com.haidoan.android.stren.core.datasource.remote.di

import com.haidoan.android.stren.core.datasource.*
import com.haidoan.android.stren.core.datasource.remote.*
import com.haidoan.android.stren.core.datasource.remote.base.*
import com.haidoan.android.stren.core.datasource.remote.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Qualifier

@Module
@InstallIn(SingletonComponent::class)
internal abstract class DataSourceModule {
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

    @Binds
    abstract fun bindUserRemoteDataSource(impl: UserFirestoreDataSource): UserRemoteDataSource

    @Binds
    abstract fun bindDefaultValuesRemoteDataSource(impl: DefaultValuesFirestoreDataSource): DefaultValuesRemoteDataSource

    @AlgoliaDataSource
    @Binds
    abstract fun bindExercisesSearchDataSource(impl: ExercisesAlgoliaDataSource): ExercisesSearchDataSource
}

@Qualifier
annotation class AlgoliaDataSource