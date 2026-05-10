package com.luminaos.launcher.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.luminaos.launcher.data.model.AppInfo
import com.luminaos.launcher.data.model.LauncherSettings

@Composable
fun AppGrid(
    apps: List<AppInfo>,
    settings: LauncherSettings,
    onAppClick: (AppInfo) -> Unit,
    onAppLongClick: (AppInfo) -> Unit,
    modifier: Modifier = Modifier
) {
    val columns = settings.gridSize.columns
    val iconSize = settings.iconSize.sizeDp
    
    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(16.dp),
        modifier = modifier
    ) {
        items(apps, key = { it.packageName }) { app ->
            AppIcon(
                appInfo = app,
                showLabel = settings.showLabels,
                iconSize = iconSize,
                onClick = { onAppClick(app) },
                onLongClick = { onAppLongClick(app) }
            )
        }
    }
}
