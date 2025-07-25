package com.vikaspogu.logit.ui.entry

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.Attending
import com.vikaspogu.logit.data.model.Entry
import com.vikaspogu.logit.data.model.RegionalType
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.data.repository.AttendingRepository
import com.vikaspogu.logit.data.repository.EntryRepository
import com.vikaspogu.logit.data.repository.RegionalTypeRepository
import com.vikaspogu.logit.data.repository.TypeRepository
import com.vikaspogu.logit.data.repository.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class AddEntryViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val entryRepository: EntryRepository,
    private val typeRepository: TypeRepository,
    private val attendingRepository: AttendingRepository,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val regionalTypeRepository: RegionalTypeRepository
) : ViewModel() {

    var addEntryUiState by mutableStateOf(AddEntryUiState())

    private val _residentView = mutableStateOf(false)
    var residentView: MutableState<Boolean> = _residentView

    private val _username = mutableStateOf("")
    var username: MutableState<String> = _username

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

    private val _selectedGender = mutableStateOf("Male")
    var selectedGender: State<String> = _selectedGender
    fun updateSelectedGender(text: String) {
        _selectedGender.value = text
    }

    private val _selectedClinical= mutableStateOf(false)
    var selectedClinical: State<Boolean> = _selectedClinical
    fun updateSelectedClinical(text: Boolean) {
        _selectedClinical.value = text
    }

    private val _selectedCVC= mutableStateOf(false)
    var selectedCVC: State<Boolean> = _selectedCVC
    fun updateSelectedCVC(text: Boolean) {
        _selectedCVC.value = text
    }

    private val _selectedRegional= mutableStateOf(false)
    var selectedRegional: State<Boolean> = _selectedRegional
    fun updateSelectedRegional(text: Boolean) {
        _selectedRegional.value = text
    }

    private val _selectedRegionalType = mutableStateOf("")
    var selectedRegionalType: State<String> = _selectedRegionalType
    fun updateSelectedRegionalType(text: String) {
        _selectedRegionalType.value = text
    }

    private val _selectedDate = mutableLongStateOf(Calendar.getInstance().timeInMillis)
    var selectedDate: State<Long> = _selectedDate
    fun updateSelectedDate(date: Long) {
        _selectedDate.longValue = date
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

    val regionalTypeUiState: StateFlow<RegionalTypeUiState> = regionalTypeRepository.getAllRegionalType().map {
        RegionalTypeUiState(it)
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
        initialValue = RegionalTypeUiState()
    )

    private val entryId: String = checkNotNull(savedStateHandle["entryId"])
    val action: String = checkNotNull(savedStateHandle["action"])

    init {
        viewModelScope.launch {
            addEntryUiState = entryRepository.getEntry(entryId.toInt()).filterNotNull().first()
                .toAddEntryUiState()
            _selectedGender.value = addEntryUiState.addEntry.gender
            _selectedDate.longValue = addEntryUiState.addEntry.entryDate
            _residentView.value = userPreferencesRepository.isResidentView.first()
            _username.value = userPreferencesRepository.username.toString()
        }
    }

    fun updateEntry() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                entryRepository.updateEntry(addEntryUiState.addEntry.toEntry())
            }
        }
    }

    fun saveEntry() {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                if (addEntryUiState.addEntry.attendingId == 0 && !residentView.value){
                    addEntryUiState.addEntry.attendingId = attendingRepository.getAttendingIdByName(userPreferencesRepository.username.first().lowercase()).first()
                }
                entryRepository.insertEntry(addEntryUiState.addEntry.toEntry())
            }
        }
    }

    fun addType(procedureType: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                typeRepository.insertType(Type(0, procedureType.trim()))
            }
        }
    }

    fun addAttending(attendingName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                attendingRepository.insertAttending(Attending(0, attendingName.trim()))
            }
        }
    }

    fun addRegionalType(regionalTypeName: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                regionalTypeRepository.insertRegionalType(RegionalType(0, regionalTypeName.trim()))
            }
        }
    }

    fun updateUiState(addEntry: AddEntry) {
        addEntryUiState = AddEntryUiState(addEntry = addEntry)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class TypesUiState(val types: List<Type> = listOf())

data class AttendingUiState(val attending: List<Attending> = listOf())

data class RegionalTypeUiState(val types: List<RegionalType> = listOf())

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
    var gender: String = "",
    var asa: String = "",
    var clinical: String = "",
    var cvc: String = "",
    var regionalId: Int? = null,
)

fun AddEntry.toEntry(): Entry = Entry(
    notes = notes,
    quantity = quantity.toIntOrNull() ?: 0,
    id = id,
    entryDate = entryDate,
    gender = gender,
    attendingId = attendingId,
    age = age.toIntOrNull() ?: 0,
    typeId = typeId,
    asa = asa.toIntOrNull() ?:0,
    clinical = clinical,
    cvc = cvc,
    regionalId = regionalId,
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
    gender = gender,
    asa = asa.toString(),
    cvc = cvc,
    clinical = clinical,
    regionalId = regionalId ?:null
)