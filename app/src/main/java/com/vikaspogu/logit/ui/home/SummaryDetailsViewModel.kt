package com.vikaspogu.logit.ui.home

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vikaspogu.logit.LogItApplication
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.data.repository.EntryRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn

class SummaryDetailsViewModel(savedStateHandle: SavedStateHandle, private val entryRepository: EntryRepository) : ViewModel() {

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
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LogItApplication)
                SummaryDetailsViewModel(this.createSavedStateHandle(),application.entryRepository)
            }
        }
    }
}

data class EntryTypeUiState(val entryTypeList: List<EntryType> = listOf())