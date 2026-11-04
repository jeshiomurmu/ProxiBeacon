package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val ProxiGlassColorScheme = lightColorScheme(
    primary = ElectricUltramarine,
    onPrimary = Color.White,
    primaryContainer = ElectricUltramarineLight,
    onPrimaryContainer = ElectricUltramarine,
    secondary = BeaconEmerald,
    onSecondary = Color.White,
    secondaryContainer = Color(0x1500C853),
    onSecondaryContainer = Color(0xFF007E33),
    tertiary = BeaconAmber,
    onTertiary = Color.White,
    background = FrostWhiteBg,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardNavy,
    onSurfaceVariant = TextSecondary,
    outline = GlassCardStroke,
    outlineVariant = Color(0x100000F8)
)

@Composable
fun ProxiBeaconTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = ProxiGlassColorScheme,
        typography = Typography,
        content = content
    )
}
