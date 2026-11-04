package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BroadcastOnPersonal
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.ui.theme.BeaconEmerald
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun MyBeaconBar(
    myBeacon: MyBeaconProfile,
    onToggleActive: (Boolean) -> Unit,
    onEditClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isActive = myBeacon.isActive
    val vibe = myBeacon.vibe

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("my_beacon_bar"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isActive) CardNavy else SurfaceNavy
        ),
        border = BorderStroke(
            1.5.dp,
            if (isActive) BeaconCyan else BorderNavy
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(if (isActive) BeaconCyan.copy(alpha = 0.2f) else DeepNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.BroadcastOnPersonal,
                            contentDescription = "My Beacon",
                            tint = if (isActive) BeaconCyan else TextMuted,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = myBeacon.handle,
                                color = TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            IconButton(
                                onClick = onEditClick,
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Beacon",
                                    tint = BeaconCyan,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Text(
                            text = if (isActive) "Broadcasting Signal" else "Beacon Dormant (Not visible)",
                            color = if (isActive) BeaconEmerald else TextMuted,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Switch
                Switch(
                    checked = isActive,
                    onCheckedChange = onToggleActive,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = DeepNavy,
                        checkedTrackColor = BeaconCyan,
                        uncheckedThumbColor = TextMuted,
                        uncheckedTrackColor = SurfaceNavy
                    ),
                    modifier = Modifier.testTag("beacon_toggle_switch")
                )
            }

            AnimatedVisibility(visible = isActive) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp)
                ) {
                    // Status info bar
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(SurfaceNavy)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Current Vibe
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "${vibe.emoji} ${vibe.title}",
                                color = vibe.color,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        // Countdown timer
                        val minutes = myBeacon.remainingSeconds / 60
                        val seconds = myBeacon.remainingSeconds % 60
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Timer,
                                contentDescription = "Auto expire timer",
                                tint = TextSecondary,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = String.format("%02d:%02d left", minutes, seconds),
                                color = TextSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    if (myBeacon.tableLocation.isNotBlank() || myBeacon.topic.isNotBlank()) {
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.Place,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "${myBeacon.tableLocation} • \"${myBeacon.topic.take(35)}\"",
                                color = TextMuted,
                                fontSize = 11.sp,
                                maxLines = 1
                            )
                        }
                    }
                }
            }
        }
    }
}
