package com.example.carcare.screens


import android.Manifest
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.carcare.components.MapViewComposable
import com.example.carcare.data.model.PoiCategory
import com.example.carcare.data.model.Workshop
import com.example.carcare.viewmodels.EmergencyViewModel
import org.osmdroid.util.GeoPoint

// --- Color Palette ---
private val DeepNavy = Color(0xFF0F0F1B)
private val MidnightBlue = Color(0xFF1D1D2F)
private val CeruleanFrost = Color(0xFF1A2980)
private val AquaCyan = Color(0xFF26D0CE)
private val PremiumAccent = Color(0xFF4A7BFF)
private val GoldAccent = Color(0xFFFFD700)
private val SuccessGreen = Color(0xFF4CAF50)
private val BackgroundGradient = Brush.verticalGradient(colors = listOf(DeepNavy, MidnightBlue))
private val TopBarGradient = Brush.horizontalGradient(colors = listOf(CeruleanFrost, AquaCyan))
private val CardGradient = Brush.linearGradient(colors = listOf(CeruleanFrost.copy(alpha = 0.3f), MidnightBlue.copy(alpha = 0.7f)))
private val ButtonGradient = Brush.horizontalGradient(colors = listOf(PremiumAccent, AquaCyan))

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyScreen(
    onBack: () -> Unit = {},
    viewModel: EmergencyViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        viewModel.onPermissionResult(isGranted)
    }

    LaunchedEffect(true) {
        if (!uiState.locationPermissionGranted) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Emergency Assistance", fontWeight = FontWeight.Bold, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) { Icon(Icons.Default.ArrowBack, "Back", tint = Color.White) }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent),
                modifier = Modifier.background(TopBarGradient)
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.findNearbyPois() },
                containerColor = PremiumAccent,
                shape = CircleShape,
                modifier = Modifier.shadow(16.dp, shape = CircleShape)
            ) {
                Icon(Icons.Default.Refresh, "Refresh", tint = Color.White, modifier = Modifier.size(28.dp))
            }
        },
        containerColor = Color.Transparent
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(BackgroundGradient)
                .padding(padding)
        ) {
            CategorySelector(
                categories = uiState.availableCategories,
                selectedCategory = uiState.selectedCategory,
                onCategorySelected = { viewModel.onCategorySelected(it) },
                modifier = Modifier.padding(vertical = 12.dp)
            )

            Box(modifier = Modifier.weight(1f)) {
                if (uiState.locationPermissionGranted && uiState.userLocation != null) {
                    MapViewComposable(
                        userLocation = GeoPoint(uiState.userLocation!!.latitude, uiState.userLocation!!.longitude),
                        workshops = uiState.nearbyPois,
                        onMarkerClick = { viewModel.setSelectedPoi(it) }
                    )
                } else {
                    PermissionPrompt { permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION) }
                }

                // THE FIX: Using the full package name to avoid ambiguity
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.isLoading,
                    enter = fadeIn(),
                    exit = fadeOut(),
                    modifier = Modifier.align(Alignment.Center)
                ) {
                    CircularProgressIndicator(color = PremiumAccent)
                }

                // THE FIX: Using the full package name to avoid ambiguity
                androidx.compose.animation.AnimatedVisibility(
                    visible = uiState.nearbyPois.isNotEmpty() && uiState.selectedPoi == null && !uiState.isLoading,
                    enter = slideInVertically { it } + fadeIn(),
                    exit = slideOutVertically { it } + fadeOut(),
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    PoiListPreview(
                        pois = uiState.nearbyPois,
                        categoryName = uiState.selectedCategory.displayName,
                        onPoiSelected = { viewModel.setSelectedPoi(it) }
                    )
                }

                uiState.selectedPoi?.let { poi ->
                    PoiBottomSheet(
                        poi = poi,
                        onDismiss = { viewModel.clearSelectedPoi() }
                    )
                }
            }
        }
    }
}

@Composable
fun CategorySelector(
    categories: List<PoiCategory>,
    selectedCategory: PoiCategory,
    onCategorySelected: (PoiCategory) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyRow(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(categories) { category ->
            val isSelected = category.displayName == selectedCategory.displayName
            val backgroundColor by animateColorAsState(
                targetValue = if (isSelected) PremiumAccent else MidnightBlue.copy(alpha = 0.6f),
                label = "Chip Background Color"
            )
            val contentColor by animateColorAsState(
                targetValue = if (isSelected) Color.White else Color.White.copy(alpha = 0.8f),
                label = "Chip Content Color"
            )

            Surface(
                modifier = Modifier
                    .clip(CircleShape)
                    .clickable { onCategorySelected(category) },
                color = backgroundColor,
                shape = CircleShape,
                border = BorderStroke(1.dp, PremiumAccent)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = category.icon,
                        contentDescription = null,
                        tint = contentColor,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = category.displayName,
                        fontWeight = FontWeight.Bold,
                        color = contentColor,
                        fontSize = 14.sp
                    )
                }
            }
        }
    }
}

@Composable
fun PoiListPreview(
    pois: List<Workshop>,
    categoryName: String,
    onPoiSelected: (Workshop) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth().padding(16.dp).shadow(24.dp, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(brush = CardGradient, shape = RoundedCornerShape(24.dp))
                .border(1.dp, Brush.horizontalGradient(listOf(PremiumAccent, AquaCyan)), RoundedCornerShape(24.dp))
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    "Nearby ${categoryName}s",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = PremiumAccent),
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                pois.take(3).forEach { poi ->
                    PoiPreviewItem(poi = poi, onClick = { onPoiSelected(poi) })
                    Spacer(modifier = Modifier.height(12.dp))
                }
            }
        }
    }
}

@Composable
fun PoiPreviewItem(poi: Workshop, onClick: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)
            .background(color = MidnightBlue.copy(alpha = 0.5f), shape = RoundedCornerShape(16.dp))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(poi.name, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
            Text(
                "${"%.1f".format(poi.distanceKm)} km away",
                style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f))
            )
        }
        Icon(Icons.Default.ChevronRight, "View Details", tint = PremiumAccent, modifier = Modifier.size(24.dp))
    }
}

@Composable
fun PoiBottomSheet(poi: Workshop, onDismiss: () -> Unit) {
    val context = LocalContext.current
    Card(
        modifier = Modifier.fillMaxWidth().heightIn(max = 450.dp).padding(horizontal = 8.dp, vertical = 8.dp)
            .shadow(32.dp, RoundedCornerShape(32.dp)),
        shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier.fillMaxWidth()
                .background(brush = CardGradient, shape = RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
                .border(1.dp, Brush.horizontalGradient(listOf(PremiumAccent, AquaCyan)), RoundedCornerShape(topStart = 32.dp, topEnd = 32.dp))
        ) {
            Column(modifier = Modifier.fillMaxWidth().padding(24.dp)) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Text(poi.name, style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White), modifier = Modifier.weight(1f, fill = false))
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, "Close", tint = Color.White.copy(alpha = 0.7f)) }
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Start, verticalAlignment = Alignment.CenterVertically) {
                    InfoBadge(icon = Icons.Default.Place, value = "${"%.1f".format(poi.distanceKm)} km", color = AquaCyan)
                    Spacer(modifier = Modifier.width(12.dp))
                    if (poi.phone.isNotBlank()) {
                        InfoBadge(icon = Icons.Default.Phone, value = "Contact Available", color = SuccessGreen)
                    }
                }
                Spacer(modifier = Modifier.height(24.dp))
                Text("Details", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold, color = PremiumAccent), modifier = Modifier.padding(bottom = 8.dp))
                if (poi.phone.isNotBlank()) {
                    WorkshopDetailRow(icon = Icons.Default.Phone, label = "Phone", value = poi.phone, color = AquaCyan)
                }
                WorkshopDetailRow(icon = Icons.Default.LocationOn, label = "Address", value = poi.address, color = AquaCyan)
                Spacer(modifier = Modifier.height(24.dp))
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    ActionButton(text = "Call", icon = Icons.Default.Call, color = PremiumAccent, modifier = Modifier.weight(1f), enabled = poi.phone.isNotBlank(), onClick = {
                        val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:${poi.phone}"))
                        context.startActivity(intent)
                    })
                    ActionButton(text = "Navigate", icon = Icons.Default.Navigation, color = AquaCyan, modifier = Modifier.weight(1f), onClick = {
                        val gmmIntentUri = Uri.parse("google.navigation:q=${poi.latitude},${poi.longitude}")
                        val mapIntent = Intent(Intent.ACTION_VIEW, gmmIntentUri).apply { setPackage("com.google.android.apps.maps") }
                        context.startActivity(mapIntent)
                    })
                }
            }
        }
    }
}

@Composable
fun ActionButton(
    text: String,
    icon: ImageVector,
    color: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    val contentColor = if (enabled) Color.White else Color.Gray
    val buttonBrush = if (enabled) ButtonGradient else Brush.horizontalGradient(colors = listOf(Color.DarkGray, Color.Gray))
    val borderColor = if (enabled) color else Color.Gray

    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.height(56.dp).background(brush = buttonBrush, shape = RoundedCornerShape(16.dp)),
        colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent, disabledContainerColor = Color.Transparent, contentColor = contentColor, disabledContentColor = contentColor),
        border = BorderStroke(1.dp, borderColor),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically) {
            Icon(icon, contentDescription = text, tint = contentColor)
            Spacer(Modifier.width(8.dp))
            Text(text, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
        }
    }
}

@Composable
fun PermissionPrompt(onRequestPermission: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(Icons.Default.LocationOff, contentDescription = "Location Off", tint = PremiumAccent, modifier = Modifier.size(60.dp))
        Spacer(modifier = Modifier.height(32.dp))
        Text(
            "Location Access Required",
            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold, color = Color.White),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "To find nearby places, please grant location access from the button below.",
            style = MaterialTheme.typography.bodyMedium.copy(color = Color.White.copy(alpha = 0.8f)),
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(32.dp))
        Button(
            onClick = onRequestPermission,
            modifier = Modifier.fillMaxWidth(0.8f).height(56.dp).shadow(16.dp, RoundedCornerShape(16.dp)),
            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
            border = BorderStroke(1.dp, PremiumAccent),
            shape = RoundedCornerShape(16.dp)
        ) {
            Text("Grant Permission", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, color = Color.White))
        }
    }
}

@Composable
fun InfoBadge(icon: ImageVector, value: String, color: Color) {
    Box(
        modifier = Modifier.background(color = color.copy(alpha = 0.2f), shape = RoundedCornerShape(16.dp))
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(18.dp))
            Spacer(Modifier.width(6.dp))
            Text(value, style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold, color = color))
        }
    }
}

@Composable
fun WorkshopDetailRow(icon: ImageVector, label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = label, tint = color, modifier = Modifier.size(24.dp))
        Spacer(Modifier.width(16.dp))
        Column {
            Text(label, style = MaterialTheme.typography.labelMedium.copy(color = Color.White.copy(alpha = 0.6f)))
            Text(value, style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium, color = Color.White))
        }
    }
}