package com.vikaspogu.logit.ui.type

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.vikaspogu.logit.LogItApplication
import com.vikaspogu.logit.data.model.Type
import com.vikaspogu.logit.data.repository.TypeRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.withContext

class ManageTypeViewModel(private val typeRepository: TypeRepository) : ViewModel() {
    val typeUiState: StateFlow<TypeUiState> =
        typeRepository.getAllTypes().map { TypeUiState(it) }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(TIMEOUT_MILLIS),
            initialValue = TypeUiState()
        )

    suspend fun updateType(id: Int, type: String){
        withContext(Dispatchers.IO) {
            typeRepository.updateType(Type(id, type))
        }
    }
    suspend fun addType(type: String){
        withContext(Dispatchers.IO) {
            typeRepository.insertType(Type(0, type))
        }
    }

    suspend fun deleteType(id: Int){
        typeRepository.deleteType(id)
    }

    companion object {
        private const val TIMEOUT_MILLIS = 5_000L
        val factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application =
                    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as LogItApplication)
                ManageTypeViewModel(application.typeRepository)
            }
        }
    }
}

data class TypeUiState(val typeList: List<Type> = listOf())