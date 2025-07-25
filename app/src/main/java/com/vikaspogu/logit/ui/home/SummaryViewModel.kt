package com.vikaspogu.logit.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.Summary
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(
    entryRepository: EntryRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
) : ViewModel() {

    private val _username = mutableStateOf("")
    var username: MutableState<String> = _username

    private val _residentView = mutableStateOf(false)
    var residentView: MutableState<Boolean> = _residentView

    val summaryUiState: StateFlow<SummaryUiState> = entryRepository.getSummary().map {
        SummaryUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SummaryUiState()
    )

    init {
        viewModelScope.launch {
            _username.value = userPreferencesRepository.username.first()
            _residentView.value = userPreferencesRepository.isResidentView.first()
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class SummaryUiState(val summaryList: List<Summary> = listOf())
