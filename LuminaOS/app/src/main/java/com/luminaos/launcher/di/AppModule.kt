package com.luminaos.launcher.di

import android.content.Context
import android.content.pm.PackageManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module providing application-level dependencies.
 * 
 * This module provides singleton instances of core services and utilities
 * used throughout the LuminaOS launcher application.
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    /**
     * Provides the PackageManager instance for querying installed apps.
     */
    @Provides
    @Singleton
    fun providePackageManager(@ApplicationContext context: Context): PackageManager {
        return context.packageManager
    }

    /**
     * Provides the application context.
     */
    @Provides
    @Singleton
    fun provideApplicationContext(@ApplicationContext context: Context): Context {
        return context.applicationContext
    }
}
