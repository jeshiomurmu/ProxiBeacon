package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.BroadcastOnPersonal
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Radar
import androidx.compose.material.icons.filled.Replay
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.TouchApp
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.R
import com.example.ui.theme.BeaconAmber
import com.example.ui.theme.BeaconCyan
import com.example.ui.theme.BeaconEmerald
import com.example.ui.theme.BeaconViolet
import com.example.ui.theme.BorderNavy
import com.example.ui.theme.CardNavy
import com.example.ui.theme.DeepNavy
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay

data class DemoScene(
    val title: String,
    val subtitle: String,
    val caption: String,
    val tag: String,
    val accentColor: Color,
    val durationSeconds: Int = 6
)

val DEMO_SCENES = listOf(
    DemoScene(
        title = "Sit Down at Any Café",
        subtitle = "No algorithms. No profiles. Just who is in the room.",
        caption = "You walk into a nomad coffee shop in Lisbon, Chiang Mai, or Bali. Traditional apps match you with people 50 miles away—ProxiBeacon detects only who is right here.",
        tag = "SCENE 1/5 • THE PROBLEM",
        accentColor = BeaconCyan,
        durationSeconds = 6
    ),
    DemoScene(
        title = "Broadcast Your Real-Time Vibe",
        subtitle = "One tap broadcasts an ephemeral BLE presence signal.",
        caption = "Choose your vibe: 🟢 Open to Chat, ☕ Coffee Run, or 💡 Brainstorm. Add your physical seating cue ('Window table 3') so nomads know where you are sitting without GPS.",
        tag = "SCENE 2/5 • MY BEACON",
        accentColor = BeaconEmerald,
        durationSeconds = 6
    ),
    DemoScene(
        title = "Concentric Radar Proximity",
        subtitle = "Real-time BLE hardware distance detection.",
        caption = "The rotating radar uses Bluetooth signal strength (RSSI) to map surrounding nomads into Immediate (<1.5m), Near (1.5–4m), and Mid-range zones.",
        tag = "SCENE 3/5 • RADAR SCAN",
        accentColor = BeaconCyan,
        durationSeconds = 6
    ),
    DemoScene(
        title = "Send a Proximity Wave",
        subtitle = "Spontaneous in-person hello with zero awkwardness.",
        caption = "Tap a nomad's blip and send a quick wave with an icebreaker. Both devices get a haptic ping—letting you look up, wave, or pull up a chair.",
        tag = "SCENE 4/5 • THE WAVE",
        accentColor = BeaconAmber,
        durationSeconds = 6
    ),
    DemoScene(
        title = "Auto-Expire & Ghost Mode",
        subtitle = "100% ephemeral • No logs or tracking saved.",
        caption = "When your auto-expire timer ends or you pack your laptop, your beacon shuts down automatically. Zero chat history stored, zero server tracking.",
        tag = "SCENE 5/5 • PRIVACY",
        accentColor = BeaconViolet,
        durationSeconds = 6
    )
)

@Composable
fun DemoVideoReelModal(
    onDismiss: () -> Unit,
    onLaunchInteractiveDemo: () -> Unit
) {
    var currentSceneIndex by remember { mutableIntStateOf(0) }
    var isPlaying by remember { mutableStateOf(true) }
    var sceneProgress by remember { mutableFloatStateOf(0f) }

    val currentScene = DEMO_SCENES[currentSceneIndex]

    // Video playback timer loop
    LaunchedEffect(currentSceneIndex, isPlaying) {
        if (!isPlaying) return@LaunchedEffect

        val totalDurationMs = currentScene.durationSeconds * 1000L
        val intervalMs = 50L
        val step = intervalMs.toFloat() / totalDurationMs.toFloat()

        sceneProgress = 0f
        while (sceneProgress < 1f) {
            delay(intervalMs)
            if (isPlaying) {
                sceneProgress = (sceneProgress + step).coerceAtMost(1f)
            }
        }

        // Advance to next scene or loop
        if (currentSceneIndex < DEMO_SCENES.size - 1) {
            currentSceneIndex++
            sceneProgress = 0f
        } else {
            isPlaying = false // Pauses at end of video reel
        }
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(DeepNavy)
                .pointerInput(Unit) {
                    detectTapGestures { offset ->
                        // Tap left third to go back, right two thirds to advance
                        val width = size.width
                        if (offset.x < width * 0.3f) {
                            if (currentSceneIndex > 0) {
                                currentSceneIndex--
                                sceneProgress = 0f
                            }
                        } else {
                            if (currentSceneIndex < DEMO_SCENES.size - 1) {
                                currentSceneIndex++
                                sceneProgress = 0f
                            } else {
                                isPlaying = false
                            }
                        }
                    }
                }
                .testTag("demo_video_reel_modal")
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 20.dp, vertical = 24.dp)
            ) {
                // Top Segmented Stories/Reel Progress Bars
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    DEMO_SCENES.forEachIndexed { index, _ ->
                        val progress = when {
                            index < currentSceneIndex -> 1f
                            index == currentSceneIndex -> sceneProgress
                            else -> 0f
                        }
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .weight(1f)
                                .height(3.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = BeaconCyan,
                            trackColor = BorderNavy
                        )
                    }
                }

                // Header Bar: Tag & Close
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(12.dp),
                        color = currentScene.accentColor.copy(alpha = 0.15f),
                        border = BorderStroke(1.dp, currentScene.accentColor.copy(alpha = 0.6f))
                    ) {
                        Text(
                            text = currentScene.tag,
                            color = currentScene.accentColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        // Play/Pause button
                        IconButton(
                            onClick = { isPlaying = !isPlaying },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                                contentDescription = if (isPlaying) "Pause" else "Play",
                                tint = TextPrimary
                            )
                        }

                        IconButton(
                            onClick = onDismiss,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Close",
                                tint = TextMuted
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Animated Scene Visual Container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .clip(RoundedCornerShape(20.dp))
                        .background(SurfaceNavy)
                        .borderBorder()
                ) {
                    AnimatedContent(
                        targetState = currentSceneIndex,
                        transitionSpec = {
                            if (targetState > initialState) {
                                (slideInHorizontally { width -> width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> -width } + fadeOut()
                                )
                            } else {
                                (slideInHorizontally { width -> -width } + fadeIn()).togetherWith(
                                    slideOutHorizontally { width -> width } + fadeOut()
                                )
                            }
                        },
                        label = "SceneAnimation"
                    ) { sceneIdx ->
                        when (sceneIdx) {
                            0 -> Scene1ProblemVisual()
                            1 -> Scene2BroadcastVisual()
                            2 -> Scene3RadarVisual()
                            3 -> Scene4WaveVisual()
                            4 -> Scene5PrivacyVisual()
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Subtitle & Captions
                Text(
                    text = currentScene.title,
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = currentScene.subtitle,
                    color = currentScene.accentColor,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = currentScene.caption,
                    color = TextSecondary,
                    fontSize = 13.sp,
                    lineHeight = 19.sp
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Bottom Action Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Previous Button
                    IconButton(
                        onClick = {
                            if (currentSceneIndex > 0) {
                                currentSceneIndex--
                                sceneProgress = 0f
                            }
                        },
                        enabled = currentSceneIndex > 0,
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardNavy)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Previous scene",
                            tint = if (currentSceneIndex > 0) TextPrimary else TextMuted
                        )
                    }

                    // Main Action: Launch Interactive Demo or Next
                    Button(
                        onClick = {
                            if (currentSceneIndex < DEMO_SCENES.size - 1) {
                                currentSceneIndex++
                                sceneProgress = 0f
                            } else {
                                onLaunchInteractiveDemo()
                            }
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(46.dp)
                            .testTag("demo_reel_action_btn"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = BeaconCyan,
                            contentColor = DeepNavy
                        )
                    ) {
                        if (currentSceneIndex == DEMO_SCENES.size - 1) {
                            Icon(
                                imageVector = Icons.Default.Sensors,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Launch Live Café Radar", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        } else {
                            Text("Next Scene", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                contentDescription = null,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    // Replay button
                    IconButton(
                        onClick = {
                            currentSceneIndex = 0
                            sceneProgress = 0f
                            isPlaying = true
                        },
                        modifier = Modifier
                            .size(46.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardNavy)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Replay,
                            contentDescription = "Replay Video Reel",
                            tint = TextPrimary
                        )
                    }
                }
            }
        }
    }
}

private fun Modifier.borderBorder(): Modifier = this.then(
    BorderStroke(1.dp, BorderNavy).let { Modifier }
)

// Scene 1: Problem Graphic with the Generated Nomad Cafe Hero image
@Composable
private fun Scene1ProblemVisual() {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.nomad_cafe_hero_1793769536436),
            contentDescription = "Nomad café setting",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            DeepNavy.copy(alpha = 0.85f),
                            DeepNavy
                        )
                    )
                )
        )
        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = CardNavy.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, BeaconCyan.copy(alpha = 0.4f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "☕ Nomad Café Terrace", color = BeaconCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = "• 10+ laptops nearby", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }
    }
}

// Scene 2: Broadcasting BLE presence pulse
@Composable
private fun Scene2BroadcastVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "BroadcastAnim")
    val pulseRing by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRing"
    )
    val ringAlpha by infiniteTransition.animateFloat(
        initialValue = 0.9f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "RingAlpha"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxR = size.minDimension * 0.42f

            // Emitting concentric radio waves
            drawCircle(
                color = BeaconEmerald.copy(alpha = ringAlpha * 0.4f),
                radius = maxR * pulseRing,
                center = center,
                style = Stroke(width = 3.dp.toPx())
            )
            drawCircle(
                color = BeaconCyan.copy(alpha = ringAlpha * 0.7f),
                radius = maxR * pulseRing * 0.6f,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )
        }

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = CardNavy),
            border = BorderStroke(1.5.dp, BeaconEmerald),
            modifier = Modifier
                .padding(24.dp)
                .fillMaxWidth(0.85f)
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(text = "🟢", fontSize = 16.sp)
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(text = "Dev Nomad 💻", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Text(text = "ACTIVE", color = BeaconEmerald, fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "\"Window table 3 • Open to chat & coffee\"", color = TextSecondary, fontSize = 12.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.Sensors, contentDescription = null, tint = BeaconCyan, modifier = Modifier.size(14.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Broadcasting BLE Packet: 6E400001...", color = BeaconCyan, fontSize = 10.sp)
                }
            }
        }
    }
}

// Scene 3: Live Radar sweeping and discovering blips
@Composable
private fun Scene3RadarVisual() {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarDemoSweep")
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "Sweep"
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val center = Offset(size.width / 2f, size.height / 2f)
            val maxR = size.minDimension * 0.42f

            // Concentric rings
            for (i in 1..3) {
                drawCircle(
                    color = BeaconCyan.copy(alpha = 0.2f),
                    radius = (maxR / 3f) * i,
                    center = center,
                    style = Stroke(width = 1.dp.toPx())
                )
            }

            // Rotating sweep line
            val rad = Math.toRadians(sweepAngle.toDouble())
            val target = Offset(
                x = (center.x + maxR * kotlin.math.cos(rad)).toFloat(),
                y = (center.y + maxR * kotlin.math.sin(rad)).toFloat()
            )
            drawLine(
                color = BeaconCyan.copy(alpha = 0.6f),
                start = center,
                end = target,
                strokeWidth = 2.dp.toPx()
            )

            // Center device
            drawCircle(color = BeaconCyan, radius = 6.dp.toPx(), center = center)

            // Blip 1: Maya (Immediate 1.2m)
            val p1 = Offset(center.x + maxR * 0.32f, center.y - maxR * 0.25f)
            drawCircle(color = BeaconEmerald, radius = 8.dp.toPx(), center = p1)

            // Blip 2: Kai (Near 2.4m)
            val p2 = Offset(center.x - maxR * 0.55f, center.y + maxR * 0.3f)
            drawCircle(color = BeaconAmber, radius = 8.dp.toPx(), center = p2)

            // Blip 3: Elena (Mid-range 3.8m)
            val p3 = Offset(center.x + maxR * 0.65f, center.y + maxR * 0.5f)
            drawCircle(color = BeaconViolet, radius = 8.dp.toPx(), center = p3)
        }

        // Overlay callouts
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(12.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = CardNavy.copy(alpha = 0.9f),
                border = BorderStroke(1.dp, BeaconCyan)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "📡 3 Nomads Detected in Range", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Scene 4: Proximity Wave animation
@Composable
private fun Scene4WaveVisual() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Sent wave
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = CardNavy),
                border = BorderStroke(1.dp, BeaconCyan),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.WavingHand, contentDescription = null, tint = BeaconCyan, modifier = Modifier.size(24.dp))
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "You waved at Maya 🎨 (~1.2m away)", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "\"Saw you're open to chat! Coffee break?\"", color = TextSecondary, fontSize = 11.sp)
                    }
                }
            }

            // Received response
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceNavy),
                border = BorderStroke(1.dp, BeaconEmerald),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "👋", fontSize = 24.sp)
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(text = "Maya waved back! (Table by plants)", color = BeaconEmerald, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                        Text(text = "\"Yes! Pull up a chair, grabbing a flat white!\"", color = TextPrimary, fontSize = 11.sp)
                    }
                }
            }
        }
    }
}

// Scene 5: Privacy & Auto Expire
@Composable
private fun Scene5PrivacyVisual() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(BeaconViolet.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = BeaconViolet, modifier = Modifier.size(36.dp))
            }

            Text(
                text = "Zero Profiles • Zero Footprint",
                color = TextPrimary,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )

            Text(
                text = "✓ No accounts or passwords\n✓ No GPS tracking or cloud databases\n✓ Beacon auto-expires when packing up\n✓ 100% ephemeral Bluetooth radio proximity",
                color = TextSecondary,
                fontSize = 12.sp,
                lineHeight = 18.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}
