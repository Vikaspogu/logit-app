package com.vikaspogu.logit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.Summary
import com.vikaspogu.logit.data.repository.EntryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class SummaryViewModel @Inject constructor(entryRepository: EntryRepository) : ViewModel() {

    val summaryUiState: StateFlow<SummaryUiState> = entryRepository.getSummary().map {
        SummaryUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SummaryUiState()
    )

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class SummaryUiState(val summaryList: List<Summary> = listOf())
