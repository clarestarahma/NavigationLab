package com.example.navigationlab.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.navigationlab.data.model.Student
import com.example.navigationlab.ui.home.HomeUiState
import retrofit2.http.Query

@Composable
fun HomeScreen(
    uiState: HomeUiState,
    onStudentClick: (Int) -> Unit,
    onRetry: () -> Unit,
    onOpenProfile: () -> Unit,
    onOpenAbout: () -> Unit,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Home", style = MaterialTheme.typography.headlineMedium)

        Row (
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedButton(onClick = onOpenProfile) {
                Text("Profile")
            }
            TextButton(onClick = onOpenAbout) {
                Text("About")
            }
        }

        SearchBar(
            query = uiState.searchQuery,
            onSearchQueryChange = onSearchQueryChange,
            onSearch = onSearch
        )

        when {
            uiState.isLoading -> {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.errorMessage != null -> {
                Column (
                    modifier = Modifier.fillMaxSize().padding(24.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(uiState.errorMessage)
                    Button(onClick = onRetry) { Text("Coba Lagi") }
                }
            }

            else -> {
                LazyColumn (
                    modifier = Modifier.weight(1f)
                        .fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(uiState.students) { student ->
                        StudentCard(
                            student = student,
                            onClick = { onStudentClick(student.id) },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SearchBar(
    query: String,
    onSearchQueryChange: (String) -> Unit,
    onSearch: () -> Unit
){
    OutlinedTextField(
        value = query,
        onValueChange = onSearchQueryChange,
        modifier = Modifier.fillMaxWidth(),
        placeholder = { Text("Cari student...") },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search Icon"
            )
        },
        singleLine = true
    )

    Button(
        onClick = onSearch
    ) {
        Text("Cari")
    }
}

@Composable
fun StudentCard(
    student: Student,
    onClick: () -> Unit,
    modifier: Modifier
) {
    Column (
        modifier = modifier.padding(16.dp)
    ) {
        Text (
            text = student.name,
            style = MaterialTheme.typography.titleMedium
        )
        Text (
            text = "${student.university} Email: ${student.email}",
            style = MaterialTheme.typography.bodyMedium
        )
        Button (
            onClick = onClick
        ) {
            Text("Buka Detail Mahasiswa ${student.id}")
        }
    }
}