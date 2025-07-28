package com.vikaspogu.logit.ui.manage

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.RegionalType
import com.vikaspogu.logit.data.repository.RegionalTypeRepository
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
class ManageRegionalTypeViewModel @Inject constructor(private val regionalTypeRepository: RegionalTypeRepository) :
    ViewModel() {
    val regionalTypeUiState: StateFlow<RegionalTypeUiState> =
        regionalTypeRepository.getAllRegionalType().map { RegionalTypeUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = RegionalTypeUiState()
        )

    fun updateRegionalType(id: Int, type: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                regionalTypeRepository.updateRegionalType(RegionalType(id, type))
            }
        }
    }

    fun addRegionalType(type: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                regionalTypeRepository.insertRegionalType(RegionalType(0, type))
            }
        }
    }

    fun deleteRegionalType(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                regionalTypeRepository.deleteRegionalType(id)
            }
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class RegionalTypeUiState(val typeList: List<RegionalType> = listOf())