package com.vikaspogu.logit.ui.entry

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vikaspogu.logit.LogItApplication
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.TypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val typeRepository: TypeRepository
) : ViewModel() {

    var addEntryUiState by mutableStateOf(AddEntryUiState())

    val typeUiState: StateFlow<TypesUiState> =
        typeRepository.getAllTypes().map { TypesUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TypesUiState()
        )

    private val entryId: String = checkNotNull(savedStateHandle["entryId"])

    init {
        viewModelScope.launch {
            addEntryUiState = entryRepository.getEntry(entryId.toInt()).filterNotNull().first()
                .toAddEntryUiState()
        }
    }

    suspend fun updateEntry() {
        withContext(Dispatchers.IO) {
            entryRepository.updateEntry(addEntryUiState.addEntry.toEntry())
        }
    }

    fun updateUiState(addEntry: AddEntry) {
        addEntryUiState = AddEntryUiState(addEntry = addEntry)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LogItApplication)
                EditEntryViewModel(
                    this.createSavedStateHandle(),
                    application.entryRepository,
                    application.typeRepository
                )
            }
        }
    }
}
