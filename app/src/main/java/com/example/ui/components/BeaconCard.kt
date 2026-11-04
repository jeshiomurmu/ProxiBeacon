package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.filled.NearMe
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.data.model.NomadBeacon
import com.example.ui.theme.BeaconCyan
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun BeaconCard(
    beacon: NomadBeacon,
    isSelected: Boolean,
    onCardClick: () -> Unit,
    onWaveClick: () -> Unit,
    onIcebreakerClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val vibe = beacon.vibe
    val border = if (isSelected) {
        BorderStroke(1.5.dp, BeaconCyan)
    } else {
        BorderStroke(1.dp, BorderNavy)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onCardClick)
            .testTag("beacon_card_${beacon.id}"),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = CardNavy),
        border = border
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Header: Status Vibe Badge + Distance Indicator
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Vibe pill
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = vibe.color.copy(alpha = 0.18f),
                    border = BorderStroke(1.dp, vibe.color.copy(alpha = 0.5f))
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(vibe.color)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "${vibe.emoji} ${vibe.title}",
                            color = vibe.color,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                // Distance & Signal dBm
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Wifi,
                        contentDescription = "Signal Strength",
                        tint = BeaconCyan,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "~${beacon.estimatedDistanceMeters}m (${beacon.rssi} dBm)",
                        color = TextSecondary,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Nomad Handle
            Text(
                text = beacon.handle,
                color = TextPrimary,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold
            )

            // Table / Seating Location cue
            if (beacon.tableLocation.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Place,
                        contentDescription = "Location",
                        tint = TextMuted,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = beacon.tableLocation,
                        color = TextMuted,
                        fontSize = 12.sp
                    )
                }
            }

            // Topic / Discussion Hook
            if (beacon.topic.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = SurfaceNavy.copy(alpha = 0.7f),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "\"${beacon.topic}\"",
                        color = TextSecondary,
                        fontSize = 13.sp,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
                    )
                }
            }

            // Quick Icebreakers if expanded or available
            if (beacon.icebreakers.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Text(
                    text = "QUICK ICEBREAKER HOOKS:",
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
                Spacer(modifier = Modifier.height(6.dp))
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    beacon.icebreakers.forEach { hook ->
                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = SurfaceNavy,
                            border = BorderStroke(1.dp, BorderNavy),
                            modifier = Modifier.clickable { onIcebreakerClick(hook) }
                        ) {
                            Text(
                                text = "💬 $hook",
                                color = BeaconCyan,
                                fontSize = 11.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Action Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onWaveClick,
                    modifier = Modifier
                        .weight(1f)
                        .testTag("send_wave_btn_${beacon.id}"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (beacon.isDirectWaveReceived) vibe.color else BeaconCyan,
                        contentColor = DeepNavy
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.WavingHand,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = if (beacon.isDirectWaveReceived) "Wave Sent ✓" else "Send Proximity Wave",
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
