package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BroadcastOnPersonal
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.data.model.BeaconVibe
import com.example.data.model.NomadBeacon
import com.example.ui.components.BeaconCard
import com.example.ui.components.BlePermissionBanner
import com.example.ui.components.DemoVideoReelModal
import com.example.ui.components.MyBeaconBar
import com.example.ui.components.MyBeaconBroadcastSheet
import com.example.ui.components.RadarView
import com.example.ui.components.WaveModal
import com.example.ui.components.WavesLogTab
import com.example.ui.theme.BeaconAmber
import com.example.ui.theme.BeaconCyan
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.ProxiBeaconViewModel
import com.example.ui.viewmodel.ScreenTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: ProxiBeaconViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var isFilterExpanded by remember { mutableStateOf(false) }
    var isDemoReelOpen by remember { mutableStateOf(false) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = DeepNavy,
        topBar = {
            TopAppBar(
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(BeaconCyan.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                tint = BeaconCyan,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "ProxiBeacon",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                            Text(
                                text = "Zero Profiles • Pure Proximity",
                                color = BeaconCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                actions = {
                    // Quick Video Reel trigger button
                    IconButton(
                        onClick = { isDemoReelOpen = true },
                        modifier = Modifier.testTag("open_video_reel_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayCircle,
                            contentDescription = "Watch Demo Reel",
                            tint = BeaconCyan
                        )
                    }

                    // Nomad Hub Demo Simulator toggle chip
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = if (uiState.isSimulationEnabled) BeaconCyan.copy(alpha = 0.15f) else SurfaceNavy,
                        border = BorderStroke(
                            1.dp,
                            if (uiState.isSimulationEnabled) BeaconCyan.copy(alpha = 0.6f) else BorderNavy
                        ),
                        modifier = Modifier
                            .clickable { viewModel.setSimulationMode(!uiState.isSimulationEnabled) }
                            .padding(end = 4.dp)
                            .testTag("toggle_cafe_simulation_btn")
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (uiState.isSimulationEnabled) BeaconCyan else TextMuted)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (uiState.isSimulationEnabled) "Demo Hub" else "BLE Only",
                                color = if (uiState.isSimulationEnabled) BeaconCyan else TextMuted,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }

                    IconButton(
                        onClick = { isFilterExpanded = !isFilterExpanded },
                        modifier = Modifier.testTag("toggle_filters_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Tune,
                            contentDescription = "Filters",
                            tint = if (uiState.filterVibe != null || uiState.maxDistanceFilterMeters < 15f) BeaconCyan else TextMuted
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = DeepNavy
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = SurfaceNavy,
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("main_navigation_bar")
            ) {
                NavigationBarItem(
                    selected = uiState.activeTab == ScreenTab.RADAR,
                    onClick = { viewModel.setActiveTab(ScreenTab.RADAR) },
                    icon = {
                        Icon(
                            imageVector = Icons.Default.Radar,
                            contentDescription = "Proximity Radar"
                        )
                    },
                    label = { Text("Radar") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepNavy,
                        selectedTextColor = BeaconCyan,
                        indicatorColor = BeaconCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_radar")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == ScreenTab.SURROUNDING_LIST,
                    onClick = { viewModel.setActiveTab(ScreenTab.SURROUNDING_LIST) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.filteredBeacons.isNotEmpty()) {
                                    Badge(
                                        containerColor = BeaconCyan,
                                        contentColor = DeepNavy
                                    ) {
                                        Text("${uiState.filteredBeacons.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.List,
                                contentDescription = "Surrounding Nomads"
                            )
                        }
                    },
                    label = { Text("Nomads") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepNavy,
                        selectedTextColor = BeaconCyan,
                        indicatorColor = BeaconCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_list")
                )

                NavigationBarItem(
                    selected = uiState.activeTab == ScreenTab.WAVES_LOG,
                    onClick = { viewModel.setActiveTab(ScreenTab.WAVES_LOG) },
                    icon = {
                        BadgedBox(
                            badge = {
                                if (uiState.incomingWaves.isNotEmpty()) {
                                    Badge(
                                        containerColor = BeaconAmber,
                                        contentColor = DeepNavy
                                    ) {
                                        Text("${uiState.incomingWaves.size}")
                                    }
                                }
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.WavingHand,
                                contentDescription = "Waves & Pings"
                            )
                        }
                    },
                    label = { Text("Signals") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = DeepNavy,
                        selectedTextColor = BeaconCyan,
                        indicatorColor = BeaconCyan,
                        unselectedIconColor = TextMuted,
                        unselectedTextColor = TextMuted
                    ),
                    modifier = Modifier.testTag("tab_waves")
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(modifier = Modifier.fillMaxSize()) {

                // Ble Permission Handler Banner
                BlePermissionBanner(
                    onPermissionsGranted = {
                        viewModel.startScanning()
                    }
                )

                // Quick Video Demo Reel Play Bar Banner
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = CardNavy),
                    border = BorderStroke(1.dp, BeaconCyan.copy(alpha = 0.4f)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 4.dp)
                        .clickable { isDemoReelOpen = true }
                        .testTag("banner_demo_reel")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .clip(CircleShape)
                                    .background(BeaconCyan),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.PlayArrow,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text(
                                    text = "Watch 30s Animated Video Demo",
                                    color = TextPrimary,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 12.sp
                                )
                                Text(
                                    text = "Tap to see how spontaneous nomad waves work",
                                    color = BeaconCyan,
                                    fontSize = 11.sp
                                )
                            }
                        }
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = SurfaceNavy
                        ) {
                            Text(
                                text = "▶ PLAY",
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                // My Beacon Controller Bar
                MyBeaconBar(
                    myBeacon = uiState.myBeacon,
                    onToggleActive = { active -> viewModel.toggleBeaconActive(active) },
                    onEditClick = { viewModel.openBroadcastSheet() },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
                )

                // Optional Expandable Range & Vibe Filters
                AnimatedVisibility(visible = isFilterExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 4.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardNavy)
                            .padding(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Proximity Filter: Max ${uiState.maxDistanceFilterMeters.toInt()}m",
                                color = TextPrimary,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                            if (uiState.filterVibe != null || uiState.maxDistanceFilterMeters < 15f) {
                                Text(
                                    text = "Reset",
                                    color = BeaconCyan,
                                    fontSize = 11.sp,
                                    modifier = Modifier.clickable {
                                        viewModel.setFilterVibe(null)
                                        viewModel.setMaxDistanceFilter(15f)
                                    }
                                )
                            }
                        }

                        Slider(
                            value = uiState.maxDistanceFilterMeters,
                            onValueChange = { viewModel.setMaxDistanceFilter(it) },
                            valueRange = 2f..15f,
                            steps = 12,
                            colors = SliderDefaults.colors(
                                thumbColor = BeaconCyan,
                                activeTrackColor = BeaconCyan,
                                inactiveTrackColor = BorderNavy
                            )
                        )

                        // Vibe filter horizontal chips
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .horizontalScroll(rememberScrollState()),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            FilterChip(
                                selected = uiState.filterVibe == null,
                                onClick = { viewModel.setFilterVibe(null) },
                                label = { Text("All Vibes") },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = BeaconCyan,
                                    selectedLabelColor = DeepNavy,
                                    containerColor = SurfaceNavy,
                                    labelColor = TextSecondary
                                )
                            )
                            BeaconVibe.entries.forEach { vibe ->
                                FilterChip(
                                    selected = uiState.filterVibe == vibe,
                                    onClick = { viewModel.setFilterVibe(if (uiState.filterVibe == vibe) null else vibe) },
                                    label = { Text("${vibe.emoji} ${vibe.title}") },
                                    colors = FilterChipDefaults.filterChipColors(
                                        selectedContainerColor = vibe.color,
                                        selectedLabelColor = DeepNavy,
                                        containerColor = SurfaceNavy,
                                        labelColor = TextSecondary
                                    )
                                )
                            }
                        }
                    }
                }

                // Tab Content
                when (uiState.activeTab) {
                    ScreenTab.RADAR -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .weight(1f)
                        ) {
                            Column(modifier = Modifier.fillMaxSize()) {
                                // Radar Canvas
                                RadarView(
                                    surroundingBeacons = uiState.filteredBeacons,
                                    selectedBeacon = uiState.selectedBeacon,
                                    isMyBeaconActive = uiState.myBeacon.isActive,
                                    onBeaconSelected = { beacon ->
                                        viewModel.selectBeacon(beacon)
                                    },
                                    modifier = Modifier.weight(1f)
                                )

                                // Instructions / Tap Hint
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .background(SurfaceNavy)
                                        .padding(horizontal = 16.dp, vertical = 8.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = "📡 Tap any blip on radar to inspect & wave",
                                        color = TextMuted,
                                        fontSize = 11.sp
                                    )
                                    Text(
                                        text = "${uiState.filteredBeacons.size} nomads in range",
                                        color = BeaconCyan,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            // Selected Beacon popup card over radar
                            androidx.compose.animation.AnimatedVisibility(
                                visible = uiState.selectedBeacon != null,
                                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .padding(16.dp)
                            ) {
                                uiState.selectedBeacon?.let { beacon ->
                                    Box {
                                        BeaconCard(
                                            beacon = beacon,
                                            isSelected = true,
                                            onCardClick = {},
                                            onWaveClick = { viewModel.openWaveModal(beacon) },
                                            onIcebreakerClick = { hook ->
                                                viewModel.openWaveModal(beacon)
                                            }
                                        )
                                        IconButton(
                                            onClick = { viewModel.selectBeacon(null) },
                                            modifier = Modifier
                                                .align(Alignment.TopEnd)
                                                .padding(6.dp)
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Close,
                                                contentDescription = "Close",
                                                tint = TextMuted,
                                                modifier = Modifier.size(18.dp)
                                            )
                                        }
                                    }
                                }
                            }
                        }
                    }

                    ScreenTab.SURROUNDING_LIST -> {
                        if (uiState.filteredBeacons.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Icon(
                                        imageVector = Icons.Default.NearMe,
                                        contentDescription = null,
                                        tint = TextMuted,
                                        modifier = Modifier.size(48.dp)
                                    )
                                    Spacer(modifier = Modifier.height(12.dp))
                                    Text(
                                        text = "No nomads match filter",
                                        color = TextPrimary,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "Try increasing your proximity range or toggling Café Hub Demo mode at the top.",
                                        color = TextMuted,
                                        fontSize = 12.sp,
                                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                                    )
                                }
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(horizontal = 16.dp),
                                verticalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                item {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "SURROUNDING SIGNALS (${uiState.filteredBeacons.size})",
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        letterSpacing = 0.5.sp
                                    )
                                }

                                items(
                                    items = uiState.filteredBeacons.sortedBy { it.estimatedDistanceMeters },
                                    key = { it.id }
                                ) { beacon ->
                                    BeaconCard(
                                        beacon = beacon,
                                        isSelected = uiState.selectedBeacon?.id == beacon.id,
                                        onCardClick = { viewModel.selectBeacon(beacon) },
                                        onWaveClick = { viewModel.openWaveModal(beacon) },
                                        onIcebreakerClick = { hook -> viewModel.openWaveModal(beacon) }
                                    )
                                }

                                item {
                                    Spacer(modifier = Modifier.height(16.dp))
                                }
                            }
                        }
                    }

                    ScreenTab.WAVES_LOG -> {
                        WavesLogTab(
                            waves = uiState.incomingWaves,
                            onClearWaves = { viewModel.clearWaves() }
                        )
                    }
                }
            }
        }
    }

    // Modal: Animated 30-Second Video Demo Reel
    if (isDemoReelOpen) {
        DemoVideoReelModal(
            onDismiss = { isDemoReelOpen = false },
            onLaunchInteractiveDemo = {
                isDemoReelOpen = false
                viewModel.setSimulationMode(true)
                viewModel.toggleBeaconActive(true)
                val firstNomad = uiState.filteredBeacons.firstOrNull()
                if (firstNomad != null) {
                    viewModel.selectBeacon(firstNomad)
                    viewModel.sendWave(firstNomad, "👋 Saw your beacon from window table! Coffee break?")
                }
            }
        )
    }

    // Modal: My Beacon broadcast configurator
    if (uiState.isBroadcastSheetOpen) {
        MyBeaconBroadcastSheet(
            myBeacon = uiState.myBeacon,
            onDismiss = { viewModel.closeBroadcastSheet() },
            onSave = { handle, vibe, topic, tableLocation, autoExpireMinutes ->
                viewModel.updateMyBeacon(handle, vibe, topic, tableLocation, autoExpireMinutes)
            }
        )
    }

    // Modal: Send Proximity Wave
    if (uiState.isWaveModalOpen && uiState.selectedBeacon != null) {
        WaveModal(
            targetBeacon = uiState.selectedBeacon!!,
            onDismiss = { viewModel.closeWaveModal() },
            onSendWave = { message ->
                viewModel.sendWave(uiState.selectedBeacon!!, message)
            }
        )
    }
}
