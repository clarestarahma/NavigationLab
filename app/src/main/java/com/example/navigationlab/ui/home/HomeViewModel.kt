package com.example.navigationlab.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navigationlab.data.repository.StudentRepository
import retrofit2.http.Query

class HomeViewModel (
    private  val repository: StudentRepository
): ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    fun loadStudents() {
        uiState = uiState.copy(isLoading = true, errorMessage = null)

        repository.getStudents { result ->
            result
                .onSuccess { data ->
                    uiState = uiState.copy(
                        isLoading = false,
                        students = data
                    )
                }
                .onFailure { error ->
                    uiState = HomeUiState(
                        errorMessage = error.message ?: "Gagal mengambil data"
                    )
                }
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        uiState = uiState.copy(searchQuery = newQuery)
    }

    fun searchStudents() {
        if (uiState.searchQuery.isBlank()) {
            loadStudents()
            return
        }

        uiState = uiState.copy(isLoading = true, errorMessage = null)

        repository.searchStudents(uiState.searchQuery) { result ->
            result
                .onSuccess { data ->
                    uiState = uiState.copy(
                        isLoading = false,
                        students = data
                    )
                }
                .onFailure { error ->
                    uiState = uiState.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Gagal mencari data"
                    )
                }
        }
    }

    companion object {
        fun factory(repository: StudentRepository): ViewModelProvider.Factory =
            object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository) as T
                }
            }
    }
}