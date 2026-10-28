package com.example.navigationlab.ui.home

import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.navigationlab.data.repository.StudentRepository

class HomeViewModel (
    private  val repository: StudentRepository
): ViewModel() {
    var uiState by mutableStateOf(HomeUiState())
        private set

    fun loadStudents() {
        val students = repository.getStudents()

        uiState = if (students != null) {
            HomeUiState(students = students)
        } else {
            HomeUiState(errorMessage = "Students tidak ditemukan")
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