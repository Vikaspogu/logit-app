package com.vikaspogu.logit.ui.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vikaspogu.logit.LogItApplication
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import com.vikaspogu.logit.ui.entry.EntriesUiState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn


class SettingsViewModel(private val entryRepository: EntryRepository,private val userPreferencesRepository: UserPreferencesRepository): ViewModel() {

    val entriesUiState: StateFlow<EntriesUiState> = entryRepository.getEntriesWithTypes().map {
        EntriesUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = EntriesUiState()
    )

    private val _selectedTheme = mutableStateOf(false)
    var selectedTheme: MutableState<Boolean> = _selectedTheme
    fun update_selectedTheme(theme: Boolean) {
        _selectedTheme.value = theme
    }

    val isDark: StateFlow<Boolean> = userPreferencesRepository.isDarkTheme.map { it }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    suspend fun saveThemePreferences(isDarkTheme: Boolean){
        userPreferencesRepository.saveThemePreferences(isDarkTheme)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LogItApplication)
                SettingsViewModel(
                    application.entryRepository,
                    application.userPreferencesRepository
                )
            }
        }
    }
}