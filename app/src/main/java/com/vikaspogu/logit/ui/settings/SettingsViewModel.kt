package com.vikaspogu.logit.ui.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import com.vikaspogu.logit.data.repository.WorkerManagerReminderRepository
import com.vikaspogu.logit.ui.entry.EntriesUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class SettingsViewModel @Inject constructor(
    entryRepository: EntryRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val workerManagerReminderRepository: WorkerManagerReminderRepository
) : ViewModel() {

    val entriesUiState: StateFlow<EntriesUiState> = entryRepository.getEntriesWithTypes().map {
        EntriesUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = EntriesUiState()
    )

    private val _selectedTheme = mutableStateOf(false)
    var selectedTheme: MutableState<Boolean> = _selectedTheme
    fun updateSelectedTheme(theme: Boolean) {
        _selectedTheme.value = theme
    }

    private val _selectedTime = mutableLongStateOf(0L)
    var selectedTime: MutableState<Long> = _selectedTime
    fun updateSelectedTime(selectedTime: Long) {
        _selectedTime.value = selectedTime
    }

    init {
        viewModelScope.launch {
            _selectedTheme.value = userPreferencesRepository.isDarkTheme.first()
            _selectedTime.value = userPreferencesRepository.selectedTime.first()
        }
    }

    val isDark: StateFlow<Boolean> = userPreferencesRepository.isDarkTheme.map { it }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val reminderDays: StateFlow<ReminderDaysUiState> =
        userPreferencesRepository.selectedDays.map { ReminderDaysUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = ReminderDaysUiState()
        )

    suspend fun saveThemePreferences(isDarkTheme: Boolean) {
        userPreferencesRepository.saveThemePreferences(isDarkTheme)
    }

    suspend fun saveReminderDaysPreferences(days: Set<String>) {
        userPreferencesRepository.saveReminderDays(days)
    }

    suspend fun saveReminderTimePreferences(reminderTime: Long) {
        userPreferencesRepository.saveReminderTime(reminderTime)
    }

    suspend fun saveScheduleReminder(reminderDays: Set<String>, reminderTime: Long) {
        workerManagerReminderRepository.scheduleReminder(reminderDays, reminderTime)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ReminderDaysUiState(val reminderDaysSet: Set<String> = setOf())