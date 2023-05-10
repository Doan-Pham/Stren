package com.haidoan.android.stren.core.repository.di

import com.haidoan.android.stren.core.repository.*
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
}

