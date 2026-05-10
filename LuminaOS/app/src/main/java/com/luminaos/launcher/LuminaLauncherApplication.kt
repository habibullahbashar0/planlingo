package com.luminaos.launcher

import android.app.Application
import android.content.Context
import dagger.hilt.android.HiltAndroidApp
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject

/**
 * Main Application class for LuminaOS Launcher.
 * 
 * This class serves as the entry point for the application and initializes
 * Hilt dependency injection. It provides application-level configuration
 * and global state management.
 * 
 * @author LuminaOS Team
 * @since 1.0.0
 */
@HiltAndroidApp
class LuminaLauncherApplication : Application() {

    @Inject
    @ApplicationContext
    lateinit var appContext: Context

    override fun onCreate() {
        super.onCreate()
        instance = this
        // Initialize any global components here
        // Hilt will automatically inject dependencies
    }

    override fun onTerminate() {
        super.onTerminate()
        // Clean up resources if needed
    }

    companion object {
        @Volatile
        private var instance: LuminaLauncherApplication? = null

        /**
         * Get the application instance safely.
         * @return The application instance or null if not initialized
         */
        fun getInstance(): LuminaLauncherApplication? = instance
    }
}
