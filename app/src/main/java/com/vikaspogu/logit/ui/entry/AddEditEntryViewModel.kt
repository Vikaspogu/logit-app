package com.vikaspogu.logit.ui.entry

import androidx.compose.runtime.State
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
import com.vikaspogu.logit.data.model.Attending
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.data.repository.AttendingRepository
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.OfflineAttendingRepository
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

class AddEntryViewModel(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val typeRepository: TypeRepository,
    private val attendingRepository: AttendingRepository
) : ViewModel() {

    var addEntryUiState by mutableStateOf(AddEntryUiState())

    private val _selectedProcedure = mutableStateOf("")
    var selectedProcedure: State<String> = _selectedProcedure
    fun updateSelectedProcedure(text: String) {
        _selectedProcedure.value = text
    }

    private val _selectedAttending = mutableStateOf("")
    var selectedAttending: State<String> = _selectedAttending
    fun updateSelectedAttending(text: String) {
        _selectedAttending.value = text
    }

    val typeUiState: StateFlow<TypesUiState> =
        typeRepository.getAllTypes().map { TypesUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TypesUiState()
        )

    val attendingUiState: StateFlow<AttendingUiState> = attendingRepository.getAllAttending().map {
        AttendingUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = AttendingUiState()
    )

    private val entryId: String = checkNotNull(savedStateHandle["entryId"])
    val action: String = checkNotNull(savedStateHandle["action"])

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

    suspend fun saveEntry() {
        withContext(Dispatchers.IO) {
            entryRepository.insertEntry(addEntryUiState.addEntry.toEntry())
        }
    }

    suspend fun addType(procedureType: String) {
        withContext(Dispatchers.IO) {
            typeRepository.insertType(Type(0, procedureType))
        }
    }

    suspend fun addAttending(attendingName: String) {
        withContext(Dispatchers.IO) {
            attendingRepository.insertAttending(Attending(0, attendingName))
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
                AddEntryViewModel(
                    this.createSavedStateHandle(),
                    application.entryRepository,
                    application.typeRepository,
                    application.attendingRepository
                )
            }
        }
    }
}

data class TypesUiState(val types: List<Type> = listOf())

data class AttendingUiState(val attending: List<Attending> = listOf())

data class AddEntryUiState(
    val addEntry: AddEntry = AddEntry()
)

data class AddEntry(
    var quantity: String = "",
    var notes: String = "",
    var entryDate: Long = System.currentTimeMillis(),
    var attendingId: Int = 0,
    var age: String = "",
    var id: Int = 0,
    var typeId: Int = 0,
    var gender: String = ""
)

fun AddEntry.toEntry(): Entry = Entry(
    notes = notes,
    quantity = quantity.toIntOrNull() ?: 0,
    id = id,
    entryDate = entryDate,
    gender = gender,
    attendingId = attendingId,
    age = age.toIntOrNull() ?: 0,
    typeId = typeId
)

fun Entry.toAddEntryUiState(): AddEntryUiState = AddEntryUiState(
    addEntry = this.toAddEntry()
)

fun Entry.toAddEntry(): AddEntry = AddEntry(
    notes = notes,
    quantity = quantity.toString(),
    entryDate = entryDate,
    attendingId = attendingId,
    age = age.toString(),
    id = id,
    typeId = typeId,
    gender = gender
)