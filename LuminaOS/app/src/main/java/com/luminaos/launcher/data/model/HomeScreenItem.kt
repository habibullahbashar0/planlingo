package com.luminaos.launcher.data.model

/**
 * Sealed class representing items that can be placed on the home screen.
 */
sealed class HomeScreenItem {
    data class App(val appInfo: AppInfo, val position: Int) : HomeScreenItem()
    data class Widget(val widgetInfo: WidgetInfo, val position: Int, val spanX: Int = 2, val spanY: Int = 2) : HomeScreenItem()
    data class Folder(val name: String, val apps: List<AppInfo>, val position: Int) : HomeScreenItem()
}
