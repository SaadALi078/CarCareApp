package com.example.carcare.Screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.R
import com.example.carcare.data.VehiclesViewModel
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen

// Color scheme
val DeepBlack = Color(0xFF060606)
val PrimaryPurple = Color(0xFFC451C9)
val AccentMagenta = Color(0xFFA9016D)
val CardBackground = Color(0xFF1C1B1F)
val LightPurple = Color(0xFFE0BCFF)

@Composable
fun AnimatedBackground(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition()
    val offset by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        )
    )
    Box(
        modifier = modifier.background(
            brush = Brush.linearGradient(
                colors = listOf(
                    DeepBlack.copy(alpha = 0.9f),
                    AccentMagenta.copy(alpha = 0.2f)
                ),
                start = Offset(offset, 0f),
                end = Offset(0f, offset)
            )
        )
    )
}

data class DashboardItem(val title: String, val iconRes: Int, val destination: Screen?)

@Composable
fun DashboardCard(
    item: DashboardItem,
    modifier: Modifier = Modifier,
    height: Dp = 150.dp
) {
    val animatedProgress = remember { Animatable(0f) }

    LaunchedEffect(Unit) {
        animatedProgress.animateTo(
            targetValue = 1f,
            animationSpec = tween(durationMillis = 500, delayMillis = 100)
        )
    }

    Card(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .clickable {
                item.destination?.let { Router.navigateTo(it) }
            }
            .graphicsLayer {
                scaleX = 0.9f + 0.1f * animatedProgress.value
                scaleY = 0.9f + 0.1f * animatedProgress.value
                alpha = 0.8f + 0.2f * animatedProgress.value
            }
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    colors = listOf(PrimaryPurple, AccentMagenta)
                ),
                shape = RoundedCornerShape(20.dp)
            ),
        colors = CardDefaults.cardColors(containerColor = CardBackground),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp),
        shape = RoundedCornerShape(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.radialGradient(
                            colors = listOf(PrimaryPurple, AccentMagenta),
                            center = Offset(0.5f, 0.5f),
                            radius = 100f
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    painter = painterResource(id = item.iconRes),
                    contentDescription = item.title,
                    tint = Color.White,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = item.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

@Composable
fun ModernBottomBar() {
    val selectedIndex = remember { mutableStateOf(0) }
    val navItems = listOf(
        Triple(R.drawable.ic_home, "Home", Screen.HomeScreen),
        Triple(R.drawable.ic_reminder, "Reminders", Screen.RemindersScreen),
        Triple(R.drawable.ic_vehicl, "Vehicles", Screen.VehiclesScreen),
        Triple(R.drawable.ic_profile, "Profile", Screen.ProfileScreen)
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(DeepBlack, DeepBlack.copy(alpha = 0.9f))
                )
            )
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            navItems.forEachIndexed { index, (iconRes, label, screen) ->
                val isSelected = selectedIndex.value == index
                val animationProgress by animateFloatAsState(
                    targetValue = if (isSelected) 1f else 0f,
                    animationSpec = tween(durationMillis = 300)
                )

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                    modifier = Modifier
                        .weight(1f)
                        .clickable {
                            selectedIndex.value = index
                            Router.navigateTo(screen)
                        }
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .graphicsLayer {
                                scaleX = 0.8f + 0.2f * animationProgress
                                scaleY = 0.8f + 0.2f * animationProgress
                                alpha = 0.7f + 0.3f * animationProgress
                            }
                            .background(
                                if (isSelected) {
                                    Brush.radialGradient(
                                        colors = listOf(PrimaryPurple, AccentMagenta),
                                        center = Offset(0.5f, 0.5f),
                                        radius = 100f
                                    )
                                } else {
                                    Brush.radialGradient(
                                        colors = listOf(Color.Transparent, Color.Transparent),
                                        center = Offset(0.5f, 0.5f),
                                        radius = 100f
                                    )
                                },
                                shape = CircleShape
                            )
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painter = painterResource(id = iconRes),
                            contentDescription = label,
                            tint = if (isSelected) Color.White else LightPurple,
                            modifier = Modifier.size(24.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = label,
                        color = if (isSelected) PrimaryPurple else LightPurple,
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        modifier = Modifier
                            .graphicsLayer {
                                alpha = 0.5f + 0.5f * animationProgress
                                scaleX = 0.8f + 0.2f * animationProgress
                                scaleY = 0.8f + 0.2f * animationProgress
                            }
                    )

                    if (isSelected) {
                        Box(
                            modifier = Modifier
                                .padding(top = 4.dp)
                                .width(60.dp)
                                .height(4.dp)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(PrimaryPurple, AccentMagenta)
                                    ),
                                    shape = RoundedCornerShape(50)
                                )
                        )
                    } else {
                        Spacer(modifier = Modifier
                            .padding(top = 4.dp)
                            .width(60.dp)
                            .height(4.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen() {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp.dp
    val vehiclesViewModel: VehiclesViewModel = viewModel()
    val state by vehiclesViewModel.state.collectAsState()

    LaunchedEffect(Unit) {
        vehiclesViewModel.loadVehicles()
    }

    val items = listOf(
        DashboardItem("Emergency Help", R.drawable.ic_emergency, null),
        DashboardItem("Maintenance Log", R.drawable.ic_repair, Screen.MaintenanceScreen),
        DashboardItem("Reminders", R.drawable.ic_reminder, Screen.RemindersScreen),
        DashboardItem("Vehicles", R.drawable.ic_vehicl, Screen.VehiclesScreen),
        DashboardItem("Profile & Settings", R.drawable.ic_profile, Screen.ProfileScreen)
    )

    Box(modifier = Modifier
        .fillMaxSize()
        .background(DeepBlack)) {
        AnimatedBackground(modifier = Modifier.matchParentSize())

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = { ModernBottomBar() },
            topBar = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(screenHeight * 0.18f)
                        .background(
                            Brush.verticalGradient(
                                0.0f to AccentMagenta,
                                0.5f to PrimaryPurple,
                                1.0f to DeepBlack.copy(alpha = 0.7f)
                            )
                        )
                        .padding(horizontal = 24.dp, vertical = 32.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Row(
                        modifier = Modifier.fillMaxSize(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = "Welcome Back",
                                color = LightPurple.copy(alpha = 0.8f),
                                fontSize = 16.sp
                            )
                            Text(
                                text = "Dashboard",
                                color = Color.White,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.ExtraBold,
                                letterSpacing = 1.sp,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .background(
                                    Brush.radialGradient(
                                        colors = listOf(PrimaryPurple, AccentMagenta),
                                        center = Offset(0.5f, 0.5f),
                                        radius = 100f
                                    ),
                                    shape = CircleShape
                                )
                                .clickable { /* Handle notification */ }
                                .padding(8.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Notifications,
                                contentDescription = "Notifications",
                                tint = Color.White,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                items(4) { index ->
                    DashboardCard(item = items[index])
                }
                item(span = { GridItemSpan(2) }) {
                    DashboardCard(item = items[4], height = 130.dp)
                }
            }
        }
    }
}

@Preview(showSystemUi = true, showBackground = true)
@Composable
fun HomeScreenPreview() {
    HomeScreen()
}