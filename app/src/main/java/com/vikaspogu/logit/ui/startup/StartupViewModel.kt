package com.vikaspogu.logit.ui.startup

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject


@HiltViewModel
class StartupViewModel @Inject constructor(
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _username = mutableStateOf("")
    var username: MutableState<String> = _username
    fun updateUsername(text: String) {
        _username.value = text
    }

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

    fun saveUsername(username: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                userPreferencesRepository.saveUsername(username.trim())
            }
        }
    }

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
}