package com.luminaos.launcher.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.luminaos.launcher.data.model.AppInfo
import com.luminaos.launcher.data.repository.AppRepository
import com.luminaos.launcher.data.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    application: Application,
    private val appRepository: AppRepository,
    private val settingsRepository: SettingsRepository
) : AndroidViewModel(application) {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init { loadApps(); observeSettings() }

    private fun loadApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val apps = appRepository.loadInstalledApps()
                _uiState.value = _uiState.value.copy(allApps = apps, filteredApps = apps, isLoading = false, error = null)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, error = e.message ?: "Failed to load apps")
            }
        }
    }

    private fun observeSettings() {
        viewModelScope.launch {
            settingsRepository.settings.collect { settings -> _uiState.value = _uiState.value.copy(settings = settings) }
        }
    }

    fun searchApps(query: String) {
        viewModelScope.launch {
            val results = if (query.isBlank()) _uiState.value.allApps else appRepository.searchApps(query)
            _uiState.value = _uiState.value.copy(filteredApps = results, searchQuery = query)
        }
    }

    fun clearSearch() { _uiState.value = _uiState.value.copy(filteredApps = _uiState.value.allApps, searchQuery = "") }

    fun launchApp(appInfo: AppInfo) {
        viewModelScope.launch {
            try {
                val intent = android.content.Intent(android.content.Intent.ACTION_MAIN).apply {
                    addCategory(android.content.Intent.CATEGORY_LAUNCHER)
                    setClassName(appInfo.packageName, appInfo.activityName)
                    addFlags(android.content.Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                getApplication<Application>().startActivity(intent)
            } catch (e: Exception) { _uiState.value = _uiState.value.copy(error = "Failed to launch app") }
        }
    }

    fun refreshApps() { loadApps() }
    fun dismissError() { _uiState.value = _uiState.value.copy(error = null) }
}

data class HomeUiState(
    val isLoading: Boolean = false,
    val allApps: List<AppInfo> = emptyList(),
    val filteredApps: List<AppInfo> = emptyList(),
    val searchQuery: String = "",
    val error: String? = null,
    val settings: com.luminaos.launcher.data.model.LauncherSettings = com.luminaos.launcher.data.model.LauncherSettings()
)
