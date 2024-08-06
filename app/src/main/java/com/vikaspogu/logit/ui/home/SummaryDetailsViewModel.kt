package com.vikaspogu.logit.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SummaryDetailsViewModel @Inject constructor(savedStateHandle: SavedStateHandle, entryRepository: EntryRepository) : ViewModel() {

    private val typeId: String = checkNotNull(savedStateHandle["typeId"])

    val entryTypeUiState: StateFlow<EntryTypeUiState> = entryRepository.getEntriesByType(typeId.toInt()).map {
        EntryTypeUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = EntryTypeUiState()
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class EntryTypeUiState(val entryTypeList: List<EntryType> = listOf())