package com.haidoan.android.stren.core.repository.di

import com.haidoan.android.stren.core.repository.*
import com.haidoan.android.stren.core.repository.base.ExercisesRepository
import com.haidoan.android.stren.core.repository.base.FoodRepository
import com.haidoan.android.stren.core.repository.base.RoutinesRepository
import com.haidoan.android.stren.core.repository.base.WorkoutsRepository
import com.haidoan.android.stren.core.repository.impl.ExercisesRepositoryImpl
import com.haidoan.android.stren.core.repository.impl.FoodRepositoryImpl
import com.haidoan.android.stren.core.repository.impl.RoutinesRepositoryImpl
import com.haidoan.android.stren.core.repository.impl.WorkoutsRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@InstallIn(ViewModelComponent::class)
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
}

