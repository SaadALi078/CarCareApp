package com.example.carcare.screens

import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carcare.components.MapViewComposable
import com.example.carcare.data.model.Workshop
import com.example.carcare.viewmodels.EmergencyViewModel
import org.osmdroid.util.GeoPoint

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onBack: () -> Unit = {},
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val locationPermissionGranted by viewModel.locationPermissionGranted.collectAsState()
    val userLocation by viewModel.userLocation.collectAsState()
    val nearbyWorkshops by viewModel.nearbyWorkshops.collectAsState()
    val selectedWorkshop by viewModel.selectedWorkshop.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        viewModel.updateLocationPermission(granted)
    }

    LaunchedEffect(true) {
        if (!locationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        if (locationPermissionGranted && userLocation != null) {
            MapViewComposable(
                userLocation = GeoPoint(userLocation!!.latitude, userLocation!!.longitude),
                workshops = nearbyWorkshops,
                onMarkerClick = { viewModel.setSelectedWorkshop(it) }
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Location permission is required to show nearby mechanics.")
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = {
                    permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                }) {
                    Text("Grant Permission")
                }
            }
        }

        selectedWorkshop?.let { workshop ->
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            ModalBottomSheet(
                onDismissRequest = { viewModel.clearSelectedWorkshop() },
                sheetState = sheetState,
                modifier = Modifier.fillMaxWidth()
            ) {
                WorkshopBottomSheet(
                    workshop = workshop,
                    onDismiss = { viewModel.clearSelectedWorkshop() }
                )
            }
        }
    }
}

@Composable
fun WorkshopBottomSheet(
    workshop: Workshop,
    onDismiss: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(text = workshop.name, style = MaterialTheme.typography.titleLarge)
        Spacer(modifier = Modifier.height(8.dp))
        Text("Rating: ${workshop.rating} ⭐")
        Text("Distance: ${workshop.distanceKm} km")

        Spacer(modifier = Modifier.height(16.dp))

        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(onClick = {
                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${workshop.phone}"))
                context.startActivity(intent)
            }) {
                Text("Call")
            }

            Button(onClick = {
                val gmmIntentUri =
                    Uri.parse("google.navigation:q=${workshop.latitude},${workshop.longitude}")
                val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri)
                mapIntent.setPackage("com.google.android.apps.maps")
                context.startActivity(mapIntent)
            }) {
                Text("Navigate")
            }
        }

        Spacer(modifier = Modifier.height(12.dp))
        TextButton(onClick = onDismiss) {
            Text("Close")
        }
    }
}
