package com.luminaos.launcher.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.luminaos.launcher.data.model.LauncherSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SettingsRepository @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val _settings = MutableStateFlow(loadSettings())
    val settings = _settings.asStateFlow()

    fun loadSettings(): LauncherSettings {
        return LauncherSettings(
            gridSize = LauncherSettings.GridSize.valueOf(prefs.getString(KEY_GRID_SIZE, "MEDIUM") ?: "MEDIUM"),
            iconSize = LauncherSettings.IconSize.valueOf(prefs.getString(KEY_ICON_SIZE, "MEDIUM") ?: "MEDIUM"),
            showLabels = prefs.getBoolean(KEY_SHOW_LABELS, true),
            wallpaperId = prefs.getString(KEY_WALLPAPER_ID, null),
            darkModeEnabled = prefs.getBoolean(KEY_DARK_MODE, false),
            animationSpeed = LauncherSettings.AnimationSpeed.valueOf(prefs.getString(KEY_ANIMATION_SPEED, "NORMAL") ?: "NORMAL"),
            hiddenApps = prefs.getStringSet(KEY_HIDDEN_APPS, emptySet()) ?: emptySet(),
            dockApps = prefs.getStringSet(KEY_DOCK_APPS, emptySet())?.toList() ?: emptyList()
        )
    }

    fun saveSettings(newSettings: LauncherSettings) {
        prefs.edit().apply {
            putString(KEY_GRID_SIZE, newSettings.gridSize.name)
            putString(KEY_ICON_SIZE, newSettings.iconSize.name)
            putBoolean(KEY_SHOW_LABELS, newSettings.showLabels)
            newSettings.wallpaperId?.let { putString(KEY_WALLPAPER_ID, it) }
            putBoolean(KEY_DARK_MODE, newSettings.darkModeEnabled)
            putString(KEY_ANIMATION_SPEED, newSettings.animationSpeed.name)
            putStringSet(KEY_HIDDEN_APPS, newSettings.hiddenApps)
            putStringSet(KEY_DOCK_APPS, newSettings.dockApps.toSet())
            apply()
        }
        _settings.value = newSettings
    }

    fun setGridSize(gridSize: LauncherSettings.GridSize) {
        saveSettings(_settings.value.copy(gridSize = gridSize))
    }

    fun toggleDarkMode(enabled: Boolean) {
        saveSettings(_settings.value.copy(darkModeEnabled = enabled))
    }

    companion object {
        private const val PREFS_NAME = "lumina_launcher_prefs"
        private const val KEY_GRID_SIZE = "grid_size"
        private const val KEY_ICON_SIZE = "icon_size"
        private const val KEY_SHOW_LABELS = "show_labels"
        private const val KEY_WALLPAPER_ID = "wallpaper_id"
        private const val KEY_DARK_MODE = "dark_mode"
        private const val KEY_ANIMATION_SPEED = "animation_speed"
        private const val KEY_HIDDEN_APPS = "hidden_apps"
        private const val KEY_DOCK_APPS = "dock_apps"
    }
}
