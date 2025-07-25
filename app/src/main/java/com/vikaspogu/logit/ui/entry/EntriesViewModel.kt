package com.vikaspogu.logit.ui.entry

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.EntryType
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class EntriesViewModel @Inject constructor(private val entryRepository: EntryRepository, userPreferencesRepository: UserPreferencesRepository) :
    ViewModel() {

    val entriesUiState: StateFlow<EntriesUiState> = entryRepository.getEntriesWithTypes().map {
        EntriesUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = EntriesUiState()
    )

    private val _residentView = mutableStateOf(false)
    var residentView: MutableState<Boolean> = _residentView

    fun deleteEntry(entryId: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                entryRepository.deleteEntry(entryId)
            }
        }
    }

    init {
        viewModelScope.launch {
            _residentView.value = userPreferencesRepository.isResidentView.first()
        }
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }

}

data class EntriesUiState(val entries: List<EntryType> = listOf())