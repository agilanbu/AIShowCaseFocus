package com.agilanbu.aishowcasefocus.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ── Brand palette ──────────────────────────────────────────────────────────
val NeonGreen    = Color(0xFF00E676)
val DarkSurface  = Color(0xFF0D0D0D)
val CardSurface  = Color(0xFF1A1A1A)
val CardBorder   = Color(0xFF2A2A2A)
val TextPrimary  = Color(0xFFEEEEEE)
val TextMuted    = Color(0xFF888888)
val AccentBlue   = Color(0xFF4FC3F7)
val AccentAmber  = Color(0xFFFFD54F)
val AccentCoral  = Color(0xFFFF7043)
val AccentPurple = Color(0xFFCE93D8)
val AccentTeal   = Color(0xFF4DB6AC)

private val DarkColors = darkColorScheme(
    primary          = NeonGreen,
    onPrimary        = Color(0xFF003314),
    secondary        = AccentBlue,
    onSecondary      = Color(0xFF001F2A),
    background       = DarkSurface,
    onBackground     = TextPrimary,
    surface          = CardSurface,
    onSurface        = TextPrimary,
    surfaceVariant   = Color(0xFF222222),
    onSurfaceVariant = TextMuted,
    outline          = CardBorder,
)

@Composable
fun AIShowcaseTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColors,
        content = content
    )
}
