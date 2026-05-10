package com.luminaos.launcher.data.repository

import android.content.Intent
import android.content.pm.PackageManager
import android.content.pm.ResolveInfo
import com.luminaos.launcher.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppRepository @Inject constructor(
    private val packageManager: PackageManager
) {
    private val _installedApps = MutableStateFlow<List<AppInfo>>(emptyList())
    val installedApps: Flow<List<AppInfo>> = _installedApps.asStateFlow()
    private var lastLoadTime: Long = 0L
    private val CACHE_DURATION_MS = 30_000L

    suspend fun loadInstalledApps(forceRefresh: Boolean = false): List<AppInfo> = withContext(Dispatchers.IO) {
        val currentTime = System.currentTimeMillis()
        if (!forceRefresh && _installedApps.value.isNotEmpty() && (currentTime - lastLoadTime) < CACHE_DURATION_MS) {
            return@withContext _installedApps.value
        }
        try {
            val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
                addCategory(Intent.CATEGORY_LAUNCHER)
            }
            val resolveInfoList = packageManager.queryIntentActivities(mainIntent, 0)
            val appList = resolveInfoList.mapNotNull { resolveInfo ->
                try {
                    val packageName = resolveInfo.activityInfo.packageName
                    val appInfo = packageManager.getApplicationInfo(packageName, 0)
                    if ((appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0 &&
                        (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) == 0) {
                        return@mapNotNull null
                    }
                    AppInfo(
                        packageName = packageName,
                        appName = resolveInfo.loadLabel(packageManager).toString().ifBlank { packageName },
                        icon = resolveInfo.loadIcon(packageManager),
                        activityName = resolveInfo.activityInfo.name,
                        isSystemApp = (appInfo.flags and android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0,
                        installTime = appInfo.firstInstallTime,
                        lastUpdateTime = appInfo.lastUpdateTime
                    )
                } catch (e: Exception) { null }
            }.sortedBy { it.appName.lowercase() }
            _installedApps.value = appList
            lastLoadTime = currentTime
            appList
        } catch (e: Exception) {
            _installedApps.value.ifEmpty { emptyList() }
        }
    }

    suspend fun searchApps(query: String): List<AppInfo> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext _installedApps.value
        val lowerQuery = query.lowercase()
        _installedApps.value.filter { 
            it.appName.lowercase().contains(lowerQuery) || it.packageName.lowercase().contains(lowerQuery) 
        }
    }

    fun getAppByPackageName(packageName: String): AppInfo? = _installedApps.value.find { it.packageName == packageName }
    fun clearCache() { lastLoadTime = 0L }
    suspend fun updateAppList() { loadInstalledApps(forceRefresh = true) }
}
