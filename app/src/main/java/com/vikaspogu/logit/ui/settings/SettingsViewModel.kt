package com.vikaspogu.logit.ui.settings

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.RoomDatabase
import com.vikaspogu.logit.data.LogItDatabase
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
    private val workerManagerReminderRepository: WorkerManagerReminderRepository,
    private val logItDatabase: LogItDatabase
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

    private val _residentView = mutableStateOf(false)
    var residentView: MutableState<Boolean> = _residentView
    fun updateSelectedView(view: Boolean) {
        _residentView.value = view
    }

    private val _selectedTime = mutableLongStateOf(0L)
    var selectedTime: MutableState<Long> = _selectedTime
    fun updateSelectedTime(selectedTime: Long) {
        _selectedTime.longValue = selectedTime
    }

    init {
        viewModelScope.launch {
            _selectedTheme.value = userPreferencesRepository.isDarkTheme.first()
            _selectedTime.longValue = userPreferencesRepository.selectedTime.first()
            _residentView.value = userPreferencesRepository.isResidentView.first()
        }
    }

    val isDark: StateFlow<Boolean> = userPreferencesRepository.isDarkTheme.map { it }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = true
    )

    val isResidentView: StateFlow<Boolean> = userPreferencesRepository.isResidentView.map { it }.stateIn(
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

    fun saveThemePreferences(isDarkTheme: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveThemePreferences(isDarkTheme)
        }
    }

    fun saveViewPreferences(isResidentView: Boolean) {
        viewModelScope.launch {
            userPreferencesRepository.saveView(isResidentView)
        }
    }

    fun saveReminderDaysPreferences(days: Set<String>) {
        viewModelScope.launch {
            userPreferencesRepository.saveReminderDays(days)
        }
    }

    fun saveReminderTimePreferences(reminderTime: Long) {
        viewModelScope.launch {
            userPreferencesRepository.saveReminderTime(reminderTime)
        }
    }

    fun saveScheduleReminder(reminderDays: Set<String>, reminderTime: Long) {
        viewModelScope.launch {
            workerManagerReminderRepository.scheduleReminder(reminderDays, reminderTime)
        }
    }

    fun clearAllTables(){
        viewModelScope.launch {
            logItDatabase.clearAllTables()
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class ReminderDaysUiState(val reminderDaysSet: Set<String> = setOf())