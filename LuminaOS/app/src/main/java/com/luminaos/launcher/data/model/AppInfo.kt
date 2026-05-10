package com.luminaos.launcher.data.model

import android.graphics.drawable.Drawable

/**
 * Data class representing an installed application.
 */
data class AppInfo(
    val packageName: String,
    val appName: String,
    val icon: Drawable,
    val activityName: String,
    val isSystemApp: Boolean = false,
    val installTime: Long = 0L,
    val lastUpdateTime: Long = 0L
) {
    fun isSameAs(other: AppInfo): Boolean = this.packageName == other.packageName
    fun hasSameContentAs(other: AppInfo): Boolean =
        this.packageName == other.packageName &&
        this.appName == other.appName &&
        this.isSystemApp == other.isSystemApp
}
