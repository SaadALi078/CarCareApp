@file:OptIn(ExperimentalMaterial3Api::class)

package com.example.carcare.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.*
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.carcare.navigation.Screen
import com.example.carcare.R
import com.example.carcare.viewmodels.FuelLog
import com.example.carcare.viewmodels.MaintenanceReminderItem
import com.example.carcare.viewmodels.VehicleDashboardViewModel
import com.example.carcare.viewmodels.VehicleItem
import kotlin.random.Random
import androidx.compose.ui.graphics.vector.ImageVector

// Premium color palette with harmonious blues and accents
private val PremiumBlue = Color(0xFF1A2980)
private val PremiumCyan = Color(0xFF26D0CE)
private val CardBackground = Color(0xFF1E223C)
private val CardBorder = Color(0xFF2A3A8C)
private val AccentColor = Color(0xFF4A7BFF)  // Changed from green to a harmonious blue
private val WarningColor = Color(0xFFFFA000)

@Composable
fun DashboardScreen(navController: NavController) {
    val viewModel: VehicleDashboardViewModel = viewModel()
    val state by viewModel.state.collectAsState()
    val scrollState = rememberScrollState()

    val backgroundGradient = Brush.verticalGradient(
        colors = listOf(Color(0xFF0F0F1B), Color(0xFF1D1D2F))
    )

    val topBarGradient = Brush.horizontalGradient(
        colors = listOf(PremiumBlue, PremiumCyan)
    )

    val contentAlpha = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        contentAlpha.animateTo(1f, animationSpec = tween(800))
    }

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()

    Scaffold(
        modifier = Modifier
            .alpha(contentAlpha.value)
            .nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "CAR CARE",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp,
                            color = Color.White
                        ),
                        modifier = Modifier.padding(start = 8.dp)
                    ) // Missing parenthesis was here
                },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: Drawer */ }) {
                        Icon(
                            Icons.Default.Menu,
                            contentDescription = "Menu",
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Notifications */ }) {
                        BadgedBox(
                            badge = {
                                Badge(
                                    containerColor = WarningColor,
                                    modifier = Modifier.size(8.dp)
                                )
                            }
                        ) {
                            Icon(
                                Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(26.dp)
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White
                ),
                scrollBehavior = scrollBehavior,
                modifier = Modifier
                    .background(topBarGradient)
            )
        },
        bottomBar = {
            BottomNavigationBar(navController, state.vehicles.firstOrNull()?.id)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundGradient)
                .verticalScroll(scrollState)
                .padding(paddingValues)
        ) {
            DashboardParticleBackground(particleCount = 30)
            AnimatedVisibility(
                visible = true,
                enter = fadeIn(animationSpec = tween(600)),
                exit = fadeOut()
            ) {
                Column(modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Your Vehicles",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = { navController.navigate(Screen.VehicleRegistration.route) },
                            border = BorderStroke(1.dp, AccentColor),
                            colors = ButtonDefaults.outlinedButtonColors(
                                contentColor = AccentColor,
                                containerColor = PremiumBlue.copy(alpha = 0.2f)
                            ),
                            modifier = Modifier.height(48.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add", modifier = Modifier.size(20.dp))
                            Text("Add Vehicle", modifier = Modifier.padding(start = 8.dp))
                        }
                    }
                    VehicleCarousel(viewModel, navController)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        if (viewModel.fuelLogs.isNotEmpty()) {
                            FuelEfficiencyCard(viewModel.fuelLogs, state.vehicles.firstOrNull()?.id, navController)
                        } else {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                        state.upcomingReminder?.let { reminder ->
                            UpcomingMaintenanceCard(reminder, navController)
                        } ?: Spacer(modifier = Modifier.weight(1f))
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 24.dp, bottom = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            "Quick Access",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Icon(
                            Icons.Default.Dashboard,
                            contentDescription = "Quick Access",
                            tint = AccentColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    QuickAccessCards(navController, state.vehicles.firstOrNull()?.id)
                    Spacer(modifier = Modifier.height(80.dp))
                }
            }
        }
    }
}

@Composable
fun DashboardParticleBackground(particleCount: Int) {
    val particles = remember { List(particleCount) { Particle() } }
    val infiniteTransition = rememberInfiniteTransition(label = "particle_anim")
    val progress by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(5000, easing = LinearEasing)
        ),
        label = "particle_progress"
    )


    Canvas(modifier = Modifier.fillMaxSize()) {
        particles.forEach { particle ->
            particle.update(this.size, progress)
            drawCircle(
                color = AccentColor.copy(alpha = particle.alpha),
                radius = particle.size,
                center = particle.position
            )
        }
    }
}

private class Particle {
    var position = Offset.Zero
    var velocity = Offset.Zero
    var size = 0f
    var alpha = 0f
    var life = 0f

    init {
        reset(Size(1000f, 1000f))
    }

    fun reset(screenSize: Size) {
        position = Offset(Random.nextFloat() * screenSize.width, Random.nextFloat() * screenSize.height)
        velocity = Offset((Random.nextFloat() - 0.5f) * 0.5f, (Random.nextFloat() - 0.5f) * 0.5f)
        size = Random.nextFloat() * 3 + 1
        alpha = Random.nextFloat() * 0.3f + 0.1f
        life = Random.nextFloat() * 100
    }

    fun update(screenSize: Size, progress: Float) {
        life += 1f
        position += velocity
        if (position.x !in 0f..screenSize.width || position.y !in 0f..screenSize.height || life > 200) {
            reset(screenSize)
        }
    }
}

@Composable
fun VehicleCarousel(viewModel: VehicleDashboardViewModel, navController: NavController) {
    val state by viewModel.state.collectAsState()
    when {
        state.isLoading -> Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = AccentColor)
        }
        state.vehicles.isEmpty() -> Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painterResource(R.drawable.ic_car_silhouette),
                contentDescription = "No Vehicles",
                modifier = Modifier
                    .size(120.dp)
                    .alpha(0.3f)
            )
            Text(
                "No vehicles added yet",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.6f),
                modifier = Modifier.padding(top = 16.dp)
            )
            TextButton(
                onClick = { navController.navigate(Screen.VehicleRegistration.route) },
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Text("Add Your First Vehicle", color = AccentColor)
            }
        }
        else -> LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) {
            items(state.vehicles) { vehicle -> VehicleCard(vehicle) }
        }
    }
}

@Composable
fun VehicleCard(vehicle: VehicleItem) {
    val borderGradient = Brush.horizontalGradient(
        colors = listOf(AccentColor, AccentColor.copy(alpha = 0.5f))
    )

    val elevation by animateDpAsState(12.dp, label = "card_elevation")

    Card(
        modifier = Modifier
            .width(300.dp)
            .height(180.dp)
            .graphicsLayer {
                shadowElevation = elevation.toPx()
                shape = RoundedCornerShape(28.dp)
                clip = true
            }
            .shadow(elevation, RoundedCornerShape(28.dp)),
        shape = RoundedCornerShape(28.dp),
        colors = CardDefaults.cardColors(containerColor = CardBackground)
    ) {
        Box(Modifier.fillMaxSize()) {
            Canvas(Modifier.matchParentSize()) {
                val patternSize = 40.dp.toPx()
                for (x in 0..(size.width / patternSize).toInt() + 1) {
                    for (y in 0..(size.height / patternSize).toInt() + 1) {
                        drawCircle(
                            AccentColor.copy(alpha = 0.05f),
                            2.dp.toPx(),
                            Offset(x * patternSize + patternSize / 2, y * patternSize + patternSize / 2)
                        )
                    }
                }
                drawRoundRect(
                    brush = borderGradient,
                    size = size,
                    cornerRadius = CornerRadius(28.dp.toPx()),
                    style = Stroke(width = 2.dp.toPx())
                )
            }
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Brush.linearGradient(listOf(AccentColor, PremiumCyan)), CircleShape)
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.DirectionsCar, "Car", tint = Color.Black, modifier = Modifier.size(20.dp))
                    }
                    Spacer(Modifier.width(16.dp))
                    Text(
                        "${vehicle.make} ${vehicle.model}",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                Row(
                    Modifier.fillMaxWidth(),
                    Arrangement.SpaceBetween,
                    Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            "CURRENT MILEAGE",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                        Text(
                            "${vehicle.mileage} km",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = AccentColor
                        )
                    }
                    PulsingIndicator()
                }
            }
        }
    }
}

@Composable
fun PulsingIndicator() {
    val infiniteTransition = rememberInfiniteTransition(label = "pulsing_indicator")
    val pulse by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 1.2f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_animation"
    )
    Box(
        modifier = Modifier
            .size(14.dp)
            .graphicsLayer {
                scaleX = pulse
                scaleY = pulse
            }
            .background(
                brush = Brush.radialGradient(
                    colors = listOf(AccentColor, AccentColor.copy(alpha = 0.7f))
                ),
                shape = CircleShape
            )
    )
}

@Composable
fun RowScope.FuelEfficiencyCard(logs: List<FuelLog>, vehicleId: String?, navController: NavController) {
    val result = calculateEfficiency(logs)
    val efficiency = result.first
    val costPerKm = result.second

    val cardGradient = Brush.linearGradient(listOf(CardBackground, Color(0xFF2A2F4D)))
    val context = LocalContext.current
    val elevation by animateDpAsState(8.dp, label = "fuel_card_elevation")

    Card(
        modifier = Modifier
            .weight(1f)
            .clickable {
                vehicleId?.let { navController.navigate("fuel/$it") } ?: Toast
                    .makeText(
                        context, "Add a vehicle first", Toast.LENGTH_SHORT
                    )
                    .show()
            }
            .shadow(elevation, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(modifier = Modifier
            .fillMaxSize()
            .background(cardGradient)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.LocalGasStation,
                        "Fuel",
                        tint = AccentColor,
                        modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Fuel Efficiency",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
                if (efficiency > 0) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(
                                    brush = Brush.verticalGradient(
                                        colors = listOf(
                                            Color(0x4D252A42),
                                            Color(0x80252A42)
                                        )
                                    ),
                                    shape = RoundedCornerShape(16.dp)
                                )
                        )
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                "%.1f".format(efficiency) + " km/L",
                                style = MaterialTheme.typography.displaySmall.copy(
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            )
                            Text(
                                "%.1f".format(costPerKm) + " PKR/km",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Add more logs to calculate",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.6f)
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun RowScope.UpcomingMaintenanceCard(reminder: MaintenanceReminderItem, navController: NavController) {
    val cardGradient = Brush.linearGradient(listOf(CardBackground, Color(0xFF2A2F4D)))
    val elevation by animateDpAsState(8.dp, label = "maintenance_card_elevation")

    Card(
        modifier = Modifier
            .weight(1f)
            .clickable {
                navController.navigate(Screen.Maintenance.withVehicleId(reminder.vehicleId))
            }
            .shadow(elevation, RoundedCornerShape(24.dp)),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(Modifier
            .fillMaxSize()
            .background(cardGradient)) {
            Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        Icons.Default.Build,
                        "Maintenance",
                        tint = WarningColor,
                        modifier = Modifier.size(28.dp))
                    Spacer(Modifier.width(12.dp))
                    Text(
                        "Upcoming Maintenance",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = Color.White
                        )
                    )
                }
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        reminder.type,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        "Due on ${reminder.dueDate}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                    LinearProgressIndicator(
                        progress = { 0.7f },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = WarningColor,
                        trackColor = WarningColor.copy(alpha = 0.2f)
                    )
                    Text(
                        "7 days remaining",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.6f),
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }
        }
    }
}

@Composable
fun QuickAccessCards(navController: NavController, vehicleId: String?) {
    val cards = listOf(
        Triple("Maintenance", Icons.Default.Build, vehicleId?.let { Screen.Maintenance.withVehicleId(it) } ?: Screen.Dashboard.route),
        Triple("Fuel Logs", Icons.Default.LocalGasStation, vehicleId?.let { "fuel/$it" } ?: Screen.Dashboard.route),
        Triple("Emergency", Icons.Default.Warning, Screen.Emergency.route),
        Triple("Profile", Icons.Default.Person, Screen.Profile.route)
    )
    Column(Modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(16.dp)) {
        cards.chunked(2).forEach { rowItems ->
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                rowItems.forEach { item ->
                    QuickAccessCard(item.first, item.second, item.third, navController)
                }
                if (rowItems.size == 1) Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun RowScope.QuickAccessCard(label: String, icon: ImageVector, route: String, navController: NavController) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val elevation by animateDpAsState(if (isPressed) 4.dp else 12.dp, label = "quick_access_elevation")

    Card(
        modifier = Modifier
            .weight(1f)
            .height(120.dp)
            .graphicsLayer {
                shadowElevation = elevation.toPx()
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { navController.navigate(route) }
            ),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = elevation),
        border = BorderStroke(1.dp, CardBorder)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.linearGradient(
                        colors = listOf(CardBackground, Color(0xFF2A2F4D))
                    ),
                    shape = RoundedCornerShape(24.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            brush = Brush.radialGradient(
                                colors = listOf(AccentColor.copy(alpha = 0.4f), Color.Transparent)
                            ),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        icon,
                        contentDescription = label,
                        tint = AccentColor,
                        modifier = Modifier.size(28.dp))
                }
                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    label,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Medium,
                        letterSpacing = 0.5.sp
                    ),
                    color = Color.White
                )
            }
        }
    }
}

@Composable
fun BottomNavigationBar(navController: NavController, vehicleId: String?) {
    val items = listOf(
        Triple("Home", Icons.Default.Home, Screen.Dashboard.route),
        Triple("Maintenance", Icons.Default.Build, vehicleId?.let { Screen.Maintenance.withVehicleId(it) } ?: Screen.Dashboard.route),
        Triple("Fuel", Icons.Default.LocalGasStation, vehicleId?.let { "fuel/$it" } ?: Screen.Dashboard.route),
        Triple("Profile", Icons.Default.Person, Screen.Profile.route)
    )
    NavigationBar(
        modifier = Modifier
            .shadow(16.dp)
            .height(70.dp),
        containerColor = CardBackground,
        tonalElevation = 8.dp
    ) {
        val currentRoute by remember(navController) {
            derivedStateOf { navController.currentBackStackEntry?.destination?.route }
        }
        items.forEach { (label, icon, route) ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId)
                        launchSingleTop = true
                    }
                },
                icon = {
                    Icon(
                        icon,
                        contentDescription = label,
                        modifier = Modifier.size(26.dp))
                },
                label = {
                    Text(
                        label,
                        style = MaterialTheme.typography.labelSmall,
                        maxLines = 1
                    )
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = AccentColor,
                    selectedTextColor = AccentColor,
                    unselectedIconColor = Color.White.copy(alpha = 0.7f),
                    unselectedTextColor = Color.White.copy(alpha = 0.7f),
                    indicatorColor = AccentColor.copy(alpha = 0.2f)
                ),
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}

private fun calculateEfficiency(logs: List<FuelLog>): Pair<Double, Double> {
    if (logs.size < 2) return 0.0 to 0.0

    val sortedLogs = logs.sortedBy { it.odometer }
    var totalDistance = 0.0
    var totalFuel = 0.0
    var totalCost = 0.0

    for (i in 1 until sortedLogs.size) {
        val distance = (sortedLogs[i].odometer - sortedLogs[i - 1].odometer).toDouble()
        if (distance > 0) {
            totalDistance += distance
            totalFuel += sortedLogs[i].amount
            totalCost += sortedLogs[i].cost
        }
    }

    if (totalDistance == 0.0 || totalFuel == 0.0) return 0.0 to 0.0

    val efficiency = totalDistance / totalFuel
    val costPerKm = totalCost / totalDistance

    return efficiency to costPerKm
}