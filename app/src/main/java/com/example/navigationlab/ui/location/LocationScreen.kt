package com.example.navigationlab.ui.location

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun LocationScreen(
    viewModel: LocationViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        val coarse = result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        val fine = result[Manifest.permission.ACCESS_FINE_LOCATION] == true
        if (coarse || fine) {
            viewModel.refreshLocation()
        } else {
            viewModel.onPermissionDenied()
        }
    }

    val hasLocationPermission = remember {
        ContextCompat.checkSelfPermission(
            context, Manifest.permission.ACCESS_COARSE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context, Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
    }

    Column(
        modifier = Modifier.padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        when (val state = uiState) {
            LocationUiState.Idle -> {
                Button(onClick = {
                    if (hasLocationPermission) {
                        viewModel.refreshLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }
                }) { Text("Ambil Lokasi Saya") }

                Text("Lokasi belum diminta")
            }

            LocationUiState.Loading -> CircularProgressIndicator()

            is LocationUiState.Success -> {
                val accessType = if (state.location.isPrecise) "Precise" else "Approximate"
                Text("Jenis Akses: $accessType")
                Text("Latitude: ${state.location.latitude}")
                Text("Longitude: ${state.location.longitude}")
                Text("Accuracy: ${state.location.accuracy ?: 0f} m")
                Text("Terakhir diperbarui: ${formatTimestamp(state.location.updatedAtMillis)}")

                Button(onClick = { viewModel.refreshLocation() }) {
                    Text("Perbarui Lokasi")
                }
            }

            LocationUiState.PermissionDenied -> {
                Text("Izin lokasi ditolak. Berikan izin lokasi untuk menampilkan koordinat.")
                Button(onClick = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_COARSE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    )
                }) {
                    Text("Minta Izin Lagi")
                }
            }

            is LocationUiState.Error -> {
                Text(state.message)
                Button(onClick = {
                    if (hasLocationPermission) {
                        viewModel.refreshLocation()
                    } else {
                        permissionLauncher.launch(
                            arrayOf(
                                Manifest.permission.ACCESS_COARSE_LOCATION,
                                Manifest.permission.ACCESS_FINE_LOCATION
                            )
                        )
                    }
                }) {
                    Text("Perbarui Lokasi")
                }
            }
        }

        Spacer(Modifier.height(8.dp))

        OutlinedButton(onClick = onBack) {
            Text("Kembali")
        }
    }
}

private fun formatTimestamp(millis: Long): String {
    val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm:ss", Locale.getDefault())
    return formatter.format(Date(millis))
}