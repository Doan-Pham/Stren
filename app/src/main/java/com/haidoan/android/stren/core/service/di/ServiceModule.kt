package com.haidoan.android.stren.core.service.di

import com.haidoan.android.stren.core.service.AuthenticationService
import com.haidoan.android.stren.core.service.AuthenticationServiceImpl
import com.haidoan.android.stren.core.service.StorageService
import com.haidoan.android.stren.core.service.StorageServiceFirebaseImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent

@Module
@InstallIn(ViewModelComponent::class)
abstract class ServiceModule {
    @Binds
    abstract fun provideAuthService(impl: AuthenticationServiceImpl): AuthenticationService

    @Binds
    abstract fun provideStorageService(impl: StorageServiceFirebaseImpl): StorageService
}