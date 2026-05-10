package com.luminaos.launcher.ui.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.luminaos.launcher.ui.components.AppGrid
import com.luminaos.launcher.ui.components.SearchBar
import com.luminaos.launcher.ui.viewmodel.HomeViewModel

@Composable
fun AppDrawerScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onAppClick: (com.luminaos.launcher.data.model.AppInfo) -> Unit,
    onBack: () -> Unit
) {
    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background.copy(alpha = 0.95f),
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Spacer(modifier = Modifier.height(16.dp))
            
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
                else -> {
                    AppGrid(
                        apps = uiState.value.filteredApps,
                        settings = uiState.value.settings,
                        onAppClick = onAppClick,
                        onAppLongClick = { /* Handle long click */ },
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }
        }
    }
}
