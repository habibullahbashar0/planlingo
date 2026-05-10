package com.luminaos.launcher.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.luminaos.launcher.data.repository.AppRepository
import com.luminaos.launcher.ui.screens.AppDrawerScreen
import com.luminaos.launcher.ui.screens.HomeScreen
import com.luminaos.launcher.ui.theme.LuminaOSTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var appRepository: AppRepository

    private var isAppDrawerOpen by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // Pre-load apps in background
        lifecycleScope.launch {
            appRepository.loadInstalledApps()
        }

        setContent {
            LuminaOSTheme {
                LauncherContent(
                    isAppDrawerOpen = isAppDrawerOpen,
                    onToggleAppDrawer = { isAppDrawerOpen = !isAppDrawerOpen },
                    onLaunchApp = { appInfo ->
                        try {
                            val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                                addCategory(android.content.Intent.CATEGORY_LAUNCHER)
                                setClassName(appInfo.packageName, appInfo.activityName)
                                addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                            }
                            startActivity(intent)
                            isAppDrawerOpen = false
                        } catch (e: Exception) {
                            // Handle error
                        }
                    }
                )
            }
        }
    }

    override fun onBackPressed() {
        if (isAppDrawerOpen) {
            isAppDrawerOpen = false
        } else {
            super.onBackPressed()
        }
    }
}

@androidx.compose.runtime.Composable
private fun LauncherContent(
    isAppDrawerOpen: Boolean,
    onToggleAppDrawer: () -> Unit,
    onLaunchApp: (com.luminaos.launcher.data.model.AppInfo) -> Unit
) {
    var showHome by remember { mutableStateOf(true) }

    androidx.compose.foundation.layout.Box(modifier = Modifier.fillMaxSize()) {
        HomeScreen(
            onNavigateToAppDrawer = onToggleAppDrawer
        )

        AnimatedVisibility(
            visible = isAppDrawerOpen,
            enter = fadeIn() + slideInVertically(initialOffsetY = { it }),
            exit = fadeOut() + slideOutVertically(targetOffsetY = { it })
        ) {
            AppDrawerScreen(
                onAppClick = onLaunchApp,
                onBack = onToggleAppDrawer
            )
        }
    }

    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxSize()
            .then(
                if (!isAppDrawerOpen) {
                    Modifier.pointerInput(Unit) {
                        detectTapGestures(
                            onDoubleTap = { onToggleAppDrawer() }
                        )
                    }
                } else {
                    Modifier
                }
            )
    )
}
