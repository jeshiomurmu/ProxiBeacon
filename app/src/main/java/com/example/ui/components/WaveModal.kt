package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WaveModal(
    targetBeacon: NomadBeacon,
    onDismiss: () -> Unit,
    onSendWave: (message: String) -> Unit
) {
    var message by remember {
        mutableStateOf("👋 Hey ${targetBeacon.handle.substringBefore(" ")}, saw your beacon! Open to a quick chat?")
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceNavy,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .testTag("wave_modal")
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.WavingHand,
                        contentDescription = null,
                        tint = BeaconCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Send Proximity Wave",
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
                IconButton(onClick = onDismiss) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextMuted)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Target Nomad Summary Pill
            Card(
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, BorderNavy),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = targetBeacon.vibe.emoji, fontSize = 20.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = targetBeacon.handle,
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "${targetBeacon.tableLocation} • ~${targetBeacon.estimatedDistanceMeters}m away",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            Text(
                text = "ICEBREAKER SUGGESTIONS (Tap to paste)",
                color = TextMuted,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
            )

            Spacer(modifier = Modifier.height(6.dp))

            val suggestions = listOf(
                "👋 Saw you're open to chat! Want to grab a quick coffee refuel?",
                "💡 Love your topic (\"${targetBeacon.topic.take(28)}\"). Mind if I say hi?",
                "☕ How are you liking the workspace / cafe here today?",
                "♟️ Up for a 5-minute break?"
            )

            suggestions.forEach { hook ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = CardNavy,
                    border = BorderStroke(1.dp, BorderNavy),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp)
                        .clickable { message = hook }
                ) {
                    Text(
                        text = hook,
                        color = TextSecondary,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Wave Note Text
            OutlinedTextField(
                value = message,
                onValueChange = { message = it.take(120) },
                label = { Text("Wave Note") },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("input_wave_message"),
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = BeaconCyan,
                    unfocusedBorderColor = BorderNavy,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary,
                    focusedContainerColor = CardNavy,
                    unfocusedContainerColor = CardNavy
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onSendWave(message) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("confirm_send_wave_btn"),
                colors = ButtonDefaults.buttonColors(
                    containerColor = BeaconCyan,
                    contentColor = DeepNavy
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Send,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Broadcast Wave to Device",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                )
            }

            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}
