package com.vikaspogu.logit.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vikaspogu.logit.LogItApplication
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.Summary
import com.vikaspogu.logit.data.repository.EntryRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SummaryViewModel(private val entryRepository: EntryRepository) : ViewModel() {

    private val _entryTypeUiState = MutableStateFlow(EntryTypeUiState(emptyList()))
    val entryTypeUiState: StateFlow<EntryTypeUiState> = _entryTypeUiState

    val summaryUiState: StateFlow<SummaryUiState> = entryRepository.getSummary().map {
        SummaryUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = SummaryUiState()
    )

    suspend fun getEntriesByType(typeId: Int) {
            entryRepository.getEntriesByType(typeId).collect{
                entries -> _entryTypeUiState.value = EntryTypeUiState(entries)
            }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LogItApplication)
                SummaryViewModel(application.entryRepository)
            }
        }
    }

}

data class SummaryUiState(val summaryList: List<Summary> = listOf())
data class EntryTypeUiState(val entryTypeList: List<Entry> = listOf())