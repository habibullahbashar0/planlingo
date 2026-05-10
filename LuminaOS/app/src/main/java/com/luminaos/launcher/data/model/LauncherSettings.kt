package com.luminaos.launcher.data.model

/**
 * Data class representing launcher settings and preferences.
 */
data class LauncherSettings(
    val gridSize: GridSize = GridSize.MEDIUM,
    val iconSize: IconSize = IconSize.MEDIUM,
    val showLabels: Boolean = true,
    val wallpaperId: String? = null,
    val darkModeEnabled: Boolean = false,
    val animationSpeed: AnimationSpeed = AnimationSpeed.NORMAL,
    val hiddenApps: Set<String> = emptySet(),
    val dockApps: List<String> = emptyList()
) {
    enum class GridSize(val rows: Int, val columns: Int) {
        SMALL(4, 4),
        MEDIUM(5, 5),
        LARGE(6, 6)
    }

    enum class IconSize(val sizeDp: Int) {
        SMALL(48),
        MEDIUM(56),
        LARGE(64)
    }

    enum class AnimationSpeed(val durationMs: Long) {
        FAST(150),
        NORMAL(300),
        SLOW(450)
    }
}
