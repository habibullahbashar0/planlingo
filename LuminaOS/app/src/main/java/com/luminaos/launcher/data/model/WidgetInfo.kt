package com.luminaos.launcher.data.model

import android.content.ComponentName

/**
 * Data class representing a widget that can be placed on the home screen.
 */
data class WidgetInfo(
    val componentName: ComponentName,
    val label: String,
    val minWidth: Int,
    val minHeight: Int,
    val previewImage: Int = 0
)
