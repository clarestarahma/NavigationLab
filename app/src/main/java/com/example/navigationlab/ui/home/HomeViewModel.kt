package com.example.navigationlab.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.navigationlab.data.model.Student
import com.example.navigationlab.data.preferences.UserPreferencesRepository
import com.example.navigationlab.data.repository.StudentRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel (
    private  val repository: StudentRepository,
    private val preferencesRepository: UserPreferencesRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    init {
        viewModelScope.launch {
            val savedQuery = preferencesRepository.lastSearchQuery.first()
            val savedSort = preferencesRepository.sortAscending.first()
            _searchQuery.value = savedQuery
            _uiState.update { it.copy(searchQuery = savedQuery) }
            _uiState.update { state ->
                state.copy(
                    searchQuery = savedQuery,
                    sortAscending = savedSort,
                    students = applySort(state.students, savedSort)
                )
            }
            observeSearchQuery()
        }
        viewModelScope.launch {
            searchQuery
                .debounce(500)
                .distinctUntilChanged()
                .collectLatest { value ->
                    preferencesRepository.saveLastSearchQuery(value)
                }
        }
    }

    private fun applySort(students: List<Student>, ascending: Boolean): List<Student> {
        return if (ascending) {
            students.sortedBy { it.name.lowercase() }
        } else {
            students.sortedByDescending { it.name.lowercase() }
        }
    }

    fun toggleSortOrder() {
        val newSort = !_uiState.value.sortAscending
        _uiState.update { state ->
            state.copy(
                sortAscending = newSort,
                students = applySort(state.students, newSort)
            )
        }
        viewModelScope.launch {
            preferencesRepository.saveSortAscending(newSort)
        }
    }

    fun loadStudents() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            try {
                val currentQuery = _searchQuery.value
                val students = if (currentQuery.isNotBlank()) {
                    repository.searchStudents(currentQuery)
                } else {
                    repository.getStudents()
                }
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        students = applySort(students, state.sortAscending)
                    )
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.message ?: "Gagal mengambil data"
                    )
                }
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
            _uiState.update { state ->
                state.copy(
                    isLoading = false,
                    students = applySort(students, state.sortAscending)
                )
            }
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
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        students = applySort(data, state.sortAscending)
                    )
                }
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
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        students = applySort(students, state.sortAscending)
                    )
                }
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
        fun factory(repository: StudentRepository, preferencesRepository: UserPreferencesRepository): ViewModelProvider.Factory =
            object: ViewModelProvider.Factory {
                @Suppress("UNCHECKED CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return HomeViewModel(repository, preferencesRepository) as T
                }
            }
    }
}