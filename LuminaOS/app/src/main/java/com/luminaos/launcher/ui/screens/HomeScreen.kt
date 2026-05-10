package com.luminaos.launcher.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luminaos.launcher.ui.components.AppGrid
import com.luminaos.launcher.ui.components.SearchBar
import com.luminaos.launcher.ui.viewmodel.HomeViewModel
import com.luminaos.launcher.ui.viewmodel.HomeUiState

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToAppDrawer: () -> Unit = {}
) {
    val uiState = viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(uiState.value.error) {
        uiState.value.error?.let { error ->
            snackbarHostState.showSnackbar(error)
            viewModel.dismissError()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Background wallpaper placeholder
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            ) {
                Spacer(modifier = Modifier.height(32.dp))
                
                SearchBar(
                    query = uiState.value.searchQuery,
                    onQueryChange = viewModel::searchApps,
                    onClear = viewModel::clearSearch,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                when {
                    uiState.value.isLoading -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator()
                        }
                    }
                    uiState.value.searchQuery.isNotEmpty() || uiState.value.filteredApps.size < uiState.value.allApps.size -> {
                        AppGrid(
                            apps = uiState.value.filteredApps,
                            settings = uiState.value.settings,
                            onAppClick = viewModel::launchApp,
                            onAppLongClick = { /* Handle long click */ },
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                    else -> {
                        // Show dock and home screen items
                        HomeScreenContent(
                            state = uiState.value,
                            onAppClick = viewModel::launchApp,
                            onNavigateToAppDrawer = onNavigateToAppDrawer
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HomeScreenContent(
    state: HomeUiState,
    onAppClick: (com.luminaos.launcher.data.model.AppInfo) -> Unit,
    onNavigateToAppDrawer: () -> Unit
) {
    // Display first few apps as favorites/dock
    val dockApps = state.allApps.take(minOf(5, state.allApps.size))
    
    Column(modifier = Modifier.fillMaxSize()) {
        Spacer(modifier = Modifier.weight(1f))
        
        // Dock area at bottom
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .background(
                    MaterialTheme.colorScheme.surface.copy(alpha = 0.8f),
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = 8.dp)
        ) {
            androidx.compose.foundation.lazy.LazyRow(
                horizontalArrangement = androidx.compose.foundation.layout.Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxSize()
            ) {
                items(dockApps.size) { index ->
                    val app = dockApps[index]
                    com.luminaos.launcher.ui.components.AppIcon(
                        appInfo = app,
                        showLabel = false,
                        iconSize = 48,
                        onClick = { onAppClick(app) }
                    )
                }
            }
        }
    }
}
