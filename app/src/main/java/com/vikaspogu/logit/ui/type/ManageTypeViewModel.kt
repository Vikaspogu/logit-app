package com.vikaspogu.logit.ui.type

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.data.repository.TypeRepository
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
class ManageTypeViewModel @Inject constructor(private val typeRepository: TypeRepository) :
    ViewModel() {
    val typeUiState: StateFlow<TypeUiState> =
        typeRepository.getAllTypes().map { TypeUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TypeUiState()
        )

    fun updateType(id: Int, type: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                typeRepository.updateType(Type(id, type))
            }
        }
    }

    fun addType(type: String) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                typeRepository.insertType(Type(0, type))
            }
        }
    }

    fun deleteType(id: Int) {
        viewModelScope.launch {
            withContext(Dispatchers.IO) {
                typeRepository.deleteType(id)
            }
        }

    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
    }
}

data class TypeUiState(val typeList: List<Type> = listOf())