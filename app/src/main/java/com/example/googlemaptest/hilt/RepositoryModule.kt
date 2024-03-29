package com.example.googlemaptest.hilt

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import repository.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object RepositoryModule {

    @Provides
    @Singleton
    fun providePositionRepository(@ApplicationContext appContext: Context): PositionRepository {
        return PositionRepositoryImpl(appContext)
    }

    @Provides
    @Singleton
    fun provideHeatRepository(@ApplicationContext appContext: Context): HeatRepository {
        return HeatRepositoryImpl(appContext)
    }
}
