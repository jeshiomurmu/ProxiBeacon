package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.BeaconVibe
import com.example.data.repository.MyBeaconProfile
import com.example.ui.theme.BeaconCyan
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.random.Random

private val RANDOM_NOMAD_HANDLES = listOf(
    "Alex ⚡ Kotlin & AI",
    "Maya 🎨 UI/UX Nomad",
    "Sam ☕ Espresso & Indie",
    "Elena 💡 Data & Cloud",
    "Jordan 🌍 Lisbon Hub",
    "Lucas 🤝 Rust & Crypto",
    "Sofia ♟️ Product & Chess",
    "Kai 🚀 Solo SaaS Builder"
)

private val SAMPLE_TABLE_CUES = listOf(
    "Window high-top table",
    "Corner booth by the plants",
    "Outdoor garden terrace",
    "Espresso bar stools",
    "Main communal table"
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyBeaconBroadcastSheet(
    myBeacon: MyBeaconProfile,
    onDismiss: () -> Unit,
    onSave: (handle: String, vibe: BeaconVibe, topic: String, tableLocation: String, autoExpireMinutes: Int) -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    var handle by remember { mutableStateOf(myBeacon.handle) }
    var selectedVibe by remember { mutableStateOf(myBeacon.vibe) }
    var topic by remember { mutableStateOf(myBeacon.topic) }
    var tableLocation by remember { mutableStateOf(myBeacon.tableLocation) }
    var autoExpireMinutes by remember { mutableIntStateOf(myBeacon.autoExpireMinutes) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = SurfaceNavy,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("broadcast_config_sheet")
        ) {
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Configure My Beacon",
                            color = TextPrimary,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "100% ephemeral • Zero accounts • BLE Broadcast only",
                            color = BeaconCyan,
                            fontSize = 11.sp
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Nomad Handle
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "EPHEMERAL HANDLE",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .clickable {
                                handle = RANDOM_NOMAD_HANDLES[Random.nextInt(RANDOM_NOMAD_HANDLES.size)]
                            }
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Casino,
                            contentDescription = "Random persona",
                            tint = BeaconCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Randomize",
                            color = BeaconCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = handle,
                    onValueChange = { handle = it.take(25) },
                    placeholder = { Text("e.g. Alex 💻 Mobile Dev") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_handle"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BeaconCyan,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(18.dp))

                // Vibe Status Selection
                Text(
                    text = "REAL-TIME VIBE STATUS",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))
            }

            // Vibe items
            items(BeaconVibe.entries.size) { index ->
                val vibe = BeaconVibe.entries[index]
                val isSelected = selectedVibe == vibe

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) vibe.color.copy(alpha = 0.15f) else CardNavy,
                    border = BorderStroke(
                        1.5.dp,
                        if (isSelected) vibe.color else BorderNavy
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .clickable { selectedVibe = vibe }
                        .testTag("vibe_option_${vibe.name}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = vibe.emoji, fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = vibe.title,
                                color = if (isSelected) vibe.color else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                            Text(
                                text = vibe.description,
                                color = TextMuted,
                                fontSize = 12.sp
                            )
                        }
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .size(22.dp)
                                    .clip(CircleShape)
                                    .background(vibe.color),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = null,
                                    tint = DeepNavy,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(16.dp))

                // Table / Seating Location Cue
                Text(
                    text = "TABLE / SEATING CUE (No GPS needed)",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = tableLocation,
                    onValueChange = { tableLocation = it.take(40) },
                    placeholder = { Text("e.g. Window table 3, corner by plants") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_table_location"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BeaconCyan,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy
                    ),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(6.dp))

                // Quick preset pills
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    SAMPLE_TABLE_CUES.take(2).forEach { cue ->
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = CardNavy,
                            border = BorderStroke(1.dp, BorderNavy),
                            modifier = Modifier.clickable { tableLocation = cue }
                        ) {
                            Text(
                                text = cue,
                                color = TextSecondary,
                                fontSize = 10.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Topic / Hook
                Text(
                    text = "WHAT I'M WORKING ON / CONVERSATION HOOK",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(6.dp))

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it.take(80) },
                    placeholder = { Text("e.g. Building an offline LLM app, ask me anything") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("input_topic"),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = BeaconCyan,
                        unfocusedBorderColor = BorderNavy,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedContainerColor = CardNavy,
                        unfocusedContainerColor = CardNavy
                    ),
                    maxLines = 2
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Auto-Expire Timer
                Text(
                    text = "AUTO-EXPIRE TIMER (Leaves no ghost signals)",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    listOf(30, 60, 120, 240).forEach { mins ->
                        val label = if (mins < 60) "${mins}m" else "${mins / 60}h"
                        val isTimerSelected = autoExpireMinutes == mins
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = if (isTimerSelected) BeaconCyan.copy(alpha = 0.2f) else CardNavy,
                            border = BorderStroke(
                                1.dp,
                                if (isTimerSelected) BeaconCyan else BorderNavy
                            ),
                            modifier = Modifier
                                .weight(1f)
                                .clickable { autoExpireMinutes = mins }
                        ) {
                            Text(
                                text = label,
                                color = if (isTimerSelected) BeaconCyan else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.padding(vertical = 10.dp),
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        onSave(handle.ifBlank { "Dev Nomad 💻" }, selectedVibe, topic, tableLocation, autoExpireMinutes)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp)
                        .testTag("save_beacon_config_btn"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = BeaconCyan,
                        contentColor = DeepNavy
                    )
                ) {
                    Text(
                        text = "Update Beacon Broadcast",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))
            }
        }
    }
}
