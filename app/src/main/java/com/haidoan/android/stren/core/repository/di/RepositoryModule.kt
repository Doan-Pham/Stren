package com.haidoan.android.stren.core.repository.di

import com.haidoan.android.stren.core.repository.*
import com.haidoan.android.stren.core.repository.base.*
import com.haidoan.android.stren.core.repository.impl.*
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@InstallIn(SingletonComponent::class)
@Module
abstract class RepositoryModule {

    @Binds
    abstract fun bindExercisesRepository(impl: ExercisesRepositoryImpl): ExercisesRepository

    @Binds
    abstract fun bindWorkoutsRepository(impl: WorkoutsRepositoryImpl): WorkoutsRepository

    @Binds
    abstract fun bindRoutinesRepository(impl: RoutinesRepositoryImpl): RoutinesRepository

    @Binds
    abstract fun bindFoodRepository(impl: FoodRepositoryImpl): FoodRepository

    @Binds
    abstract fun bindEatingDayRepository(impl: EatingDayRepositoryImpl): EatingDayRepository

    @Binds
    abstract fun bindUserRepository(impl: UserRepositoryImpl): UserRepository

    @Binds
    abstract fun bindDefaultValuesRepository(impl: DefaultValuesRepositoryImpl): DefaultValuesRepository

    @Binds
    abstract fun bindCoordinatesRepository(impl: CoordinatesRepositoryImpl): CoordinatesRepository
}

