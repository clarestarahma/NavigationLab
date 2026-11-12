package com.example.navigationlab.ui.detail

import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.navigationlab.data.model.Student
import com.example.navigationlab.data.repository.StudentRepository
import com.example.navigationlab.ui.home.HomeUiState
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DetailViewModel (
    private val repository: StudentRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(DetailUiState())
    var uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    fun loadStudent(id: Int) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val student = repository.getStudent(id)
                _uiState.value = DetailUiState(student = student)
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = DetailUiState (
                    errorMessage = e.message ?: "Data tidak tersedia"
                )
            }
        }
    }

    companion object {
        fun factory(repository: StudentRepository): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED CAST")
                override fun <T: ViewModel> create(modelClass: Class<T>): T {
                    return DetailViewModel(repository) as T
                }
            }
    }
}