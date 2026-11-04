package com.example.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.NomadBeacon
import com.example.ui.theme.BeaconCyan
import com.example.ui.theme.BeaconCyanDim
import com.example.ui.theme.CardNavy
import com.example.ui.theme.RadarRingColor
import com.example.ui.theme.RadarSweepColor
import com.example.ui.theme.SurfaceNavy
import com.example.ui.theme.TextMuted
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.hypot
import kotlin.math.sin

@Composable
fun RadarView(
    surroundingBeacons: List<NomadBeacon>,
    selectedBeacon: NomadBeacon?,
    isMyBeaconActive: Boolean,
    onBeaconSelected: (NomadBeacon) -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "RadarTransition")
    
    // Rotating sweep angle
    val sweepAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 4000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "SweepAngle"
    )

    // Pulsing ring scale
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.8f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseAlpha"
    )
    val pulseRadiusFactor by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 2200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "PulseRadius"
    )

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(340.dp)
            .background(SurfaceNavy)
            .testTag("radar_view_container"),
        contentAlignment = Alignment.Center
    ) {
        val widthPx = constraints.maxWidth.toFloat()
        val heightPx = constraints.maxHeight.toFloat()
        val centerX = widthPx / 2f
        val centerY = heightPx / 2f
        val maxRadarRadius = minOf(centerX, centerY) * 0.88f

        // Map beacons to pixel positions on radar
        val beaconPositions = remember(surroundingBeacons, maxRadarRadius, centerX, centerY) {
            surroundingBeacons.map { beacon ->
                // Normalized distance (1m to 12m)
                val normalizedDist = (beacon.estimatedDistanceMeters / 10f).coerceIn(0.18f, 0.95f)
                val radius = normalizedDist * maxRadarRadius
                val angleRad = Math.toRadians(beacon.radarAngleDeg.toDouble())
                val x = centerX + (radius * cos(angleRad)).toFloat()
                val y = centerY + (radius * sin(angleRad)).toFloat()
                Pair(beacon, Offset(x, y))
            }
        }

        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(beaconPositions) {
                    detectTapGestures { tapOffset ->
                        // Find closest beacon within 36dp touch radius
                        val tapped = beaconPositions.firstOrNull { (_, pos) ->
                            hypot((pos.x - tapOffset.x).toDouble(), (pos.y - tapOffset.y).toDouble()) <= 72.0
                        }
                        if (tapped != null) {
                            onBeaconSelected(tapped.first)
                        }
                    }
                }
        ) {
            val center = Offset(centerX, centerY)

            // Draw concentric distance circles
            val ringCount = 4
            val ringLabels = listOf("1.5m", "3m", "6m", "10m")
            for (i in 1..ringCount) {
                val r = (maxRadarRadius / ringCount) * i
                drawCircle(
                    color = RadarRingColor,
                    radius = r,
                    center = center,
                    style = Stroke(
                        width = 1.5.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 8f), 0f)
                    )
                )
            }

            // Draw crosshair axes
            drawLine(
                color = RadarRingColor,
                start = Offset(centerX - maxRadarRadius, centerY),
                end = Offset(centerX + maxRadarRadius, centerY),
                strokeWidth = 1.dp.toPx()
            )
            drawLine(
                color = RadarRingColor,
                start = Offset(centerX, centerY - maxRadarRadius),
                end = Offset(centerX, centerY + maxRadarRadius),
                strokeWidth = 1.dp.toPx()
            )

            // Expanding pulse wave
            drawCircle(
                color = BeaconCyan.copy(alpha = pulseAlpha * 0.35f),
                radius = maxRadarRadius * pulseRadiusFactor,
                center = center,
                style = Stroke(width = 2.dp.toPx())
            )

            // Rotating sweep beam
            val sweepRad = Math.toRadians(sweepAngle.toDouble())
            val sweepTarget = Offset(
                x = centerX + (maxRadarRadius * cos(sweepRad)).toFloat(),
                y = centerY + (maxRadarRadius * sin(sweepRad)).toFloat()
            )
            drawLine(
                brush = Brush.linearGradient(
                    colors = listOf(RadarSweepColor, Color.Transparent),
                    start = center,
                    end = sweepTarget
                ),
                start = center,
                end = sweepTarget,
                strokeWidth = 3.dp.toPx()
            )

            // Draw center (My Device)
            val centerColor = if (isMyBeaconActive) BeaconCyan else TextMuted
            drawCircle(
                color = centerColor.copy(alpha = 0.25f),
                radius = 24.dp.toPx(),
                center = center
            )
            drawCircle(
                color = centerColor,
                radius = 7.dp.toPx(),
                center = center
            )

            // Draw surrounding beacon blips
            beaconPositions.forEach { (beacon, pos) ->
                val isSelected = selectedBeacon?.id == beacon.id
                val vibeColor = beacon.vibe.color

                // Outer halo for selected or active
                if (isSelected) {
                    drawCircle(
                        color = Color.White.copy(alpha = 0.5f),
                        radius = 18.dp.toPx(),
                        center = pos,
                        style = Stroke(width = 2.dp.toPx())
                    )
                }

                // Vibe glow aura
                drawCircle(
                    color = vibeColor.copy(alpha = 0.3f),
                    radius = 14.dp.toPx(),
                    center = pos
                )

                // Blip core
                drawCircle(
                    color = vibeColor,
                    radius = 7.dp.toPx(),
                    center = pos
                )
            }
        }

        // Distance range labels overlay
        Text(
            text = "1.5m",
            color = TextMuted,
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = (maxRadarRadius * 0.3f).dp)
        )
        Text(
            text = "6m",
            color = TextMuted,
            fontSize = 9.sp,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(bottom = (maxRadarRadius * 0.8f).dp)
        )

        // Center label
        Text(
            text = if (isMyBeaconActive) "YOU (BROADCASTING)" else "YOU (STANDBY)",
            color = if (isMyBeaconActive) BeaconCyan else TextMuted,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.Center)
                .padding(top = 22.dp)
        )
    }
}
