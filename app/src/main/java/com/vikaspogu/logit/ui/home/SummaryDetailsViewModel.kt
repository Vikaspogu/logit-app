package com.vikaspogu.logit.ui.home

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.EntryType
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
class SummaryDetailsViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    entryRepository: EntryRepository,
    userPreferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val typeId: String = checkNotNull(savedStateHandle["typeId"])

    private val _residentView = mutableStateOf(false)
    var residentView: MutableState<Boolean> = _residentView

    val entryTypeUiState: StateFlow<EntryTypeUiState> =
        entryRepository.getEntriesByType(typeId.toInt()).map {
            EntryTypeUiState(it)
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = EntryTypeUiState()
        )

    init {
        viewModelScope.launch {
            _residentView.value = userPreferencesRepository.isResidentView.first()
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class EntryTypeUiState(val entryTypeList: List<EntryType> = listOf())