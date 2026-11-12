package com.example.navigationlab.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.navigationlab.data.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch

class HomeViewModel (
    private  val repository: StudentRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        loadStudents()
        observeSearchQuery()
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.value = HomeUiState(isLoading = true)

            try {
                val students = repository.getStudents()
                _uiState.value = HomeUiState(students = students)
            } catch (e: CancellationException){
                throw e
            } catch (e: Exception) {
                _uiState.value = HomeUiState(
                    errorMessage = e.message ?: "Gagal mengambil data"
                )
            }
        }
    }

    private fun observeSearchQuery() {
        viewModelScope.launch {
            _searchQuery
                .debounce(300L)
                .distinctUntilChanged()
                .collectLatest{ query ->
                    if (query.isNotBlank()) {
                        searchStudentsInternal(query)
                    } else {
                        loadStudents()
                    }
                }
        }
    }

    private suspend fun searchStudentsInternal(query: String) {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        try {
            val students = repository.searchStudents(query)
            _uiState.value = _uiState.value.copy(isLoading = false, students = students)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                errorMessage = e.message ?: "Gagal mencari data"
            )
        }
    }

    fun onSearchQueryChange(newQuery: String) {
        _searchQuery.value = newQuery
        _uiState.value = _uiState.value.copy(searchQuery = newQuery)
    }

    fun searchStudents() {
        val currentQuery  = _uiState.value.searchQuery

        if (currentQuery.isBlank()) {
            loadStudents()
            return
        }

        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)

            try {
                val data = repository.searchStudents(currentQuery)
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    students = data
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal mencari data"
                )
            }
        }
    }

    fun refreshStudents() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            val query = _uiState.value.searchQuery

            try {
                val students = if (query.isNotBlank()) repository.searchStudents(query) else repository.getStudents()
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    students = students
                )
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Gagal memperbarui data"
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