package com.vikaspogu.logit.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.Attending
import com.vikaspogu.logit.data.repository.AttendingRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

@HiltViewModel
class ManagePersonsViewModel @Inject constructor(private val attendingRepository: AttendingRepository) :
    ViewModel() {
    val personUiState: StateFlow<PersonsUiState> =
        attendingRepository.getAllAttending().map { PersonsUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = PersonsUiState()
        )

    fun updatePersons(id: Int, type: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                attendingRepository.updateAttending(Attending(id, type))
            }
        }
    }

    fun addPersons(type: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                attendingRepository.insertAttending(Attending(0, type))
            }
        }
    }

    fun deletePersons(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                attendingRepository.deleteAttending(id)
            }
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class PersonsUiState(val personList: List<Attending> = listOf())