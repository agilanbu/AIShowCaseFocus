package com.agilanbu.aishowcasefocus.ui.ondevice

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aishowcase.theme.*

@Composable
fun OnDeviceAIScreen(navController: NavController) {
    val accent = AccentAmber

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .systemBarsPadding()
    ) {
        AITopBar(title = "On-Device AI", accent = accent, navController = navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            ConceptCard(
                emoji = "🔒",
                title = "What is On-Device AI?",
                body  = "On-device AI runs machine learning models directly on the phone's CPU/GPU/NPU — no data is sent to a server. It's faster, private, and works offline.",
                accent = accent
            )

            Spacer(Modifier.height(20.dp))

            // ── Cloud vs On-Device comparison ──────────────────────────────
            SectionLabel("CLOUD AI  VS  ON-DEVICE AI")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                CompareCard(
                    title  = "Cloud AI",
                    emoji  = "☁️",
                    points = listOf(
                        "Needs internet",
                        "Data sent to server",
                        "Higher latency",
                        "Always up-to-date",
                        "Bigger models"
                    ),
                    color  = TextMuted,
                    modifier = Modifier.weight(1f)
                )
                CompareCard(
                    title  = "On-Device AI",
                    emoji  = "📱",
                    points = listOf(
                        "Works offline",
                        "Data stays on phone",
                        "Low latency",
                        "Private by design",
                        "Optimized size"
                    ),
                    color  = accent,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── Model size cards ───────────────────────────────────────────
            SectionLabel("MODEL SIZES IN THIS APP")
            val models = listOf(
                Triple("MobileNet V1",      "TFLite",      "~4 MB"),
                Triple("Gemini Nano",        "AICore",      "~1.8 GB"),
                Triple("Text Recognizer",   "ML Kit",      "~3 MB"),
                Triple("Pose Landmarker",   "MediaPipe",   "~3.5 MB"),
            )
            models.forEach { (name, tech, size) ->
                ModelRow(name = name, tech = tech, size = size, accent = accent)
                Spacer(Modifier.height(8.dp))
            }

            Spacer(Modifier.height(24.dp))

            // ── Hardware used ──────────────────────────────────────────────
            SectionLabel("ANDROID HARDWARE USED")
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                listOf("CPU", "GPU", "NPU / DSP").forEach { unit ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(12.dp))
                            .background(CardSurface)
                            .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(vertical = 14.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(
                                when (unit) { "CPU" -> "⚙️"; "GPU" -> "🎮"; else -> "🧩" },
                                fontSize = 22.sp
                            )
                            Spacer(Modifier.height(4.dp))
                            Text(unit, color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Animated latency visualizer ────────────────────────────────
            SectionLabel("INFERENCE LATENCY (TYPICAL)")
            LatencyBars(accent = accent)

            Spacer(Modifier.height(32.dp))
        }
    }
}

@Composable
fun CompareCard(
    title: String, emoji: String,
    points: List<String>, color: Color, modifier: Modifier
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .border(1.dp, color.copy(alpha = 0.3f), RoundedCornerShape(14.dp))
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(emoji, fontSize = 18.sp)
            Spacer(Modifier.width(6.dp))
            Text(title, color = color, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(10.dp))
        points.forEach { point ->
            Row(verticalAlignment = Alignment.Top, modifier = Modifier.padding(vertical = 2.dp)) {
                Text("•", color = color, fontSize = 12.sp, modifier = Modifier.padding(end = 6.dp, top = 1.dp))
                Text(point, color = TextPrimary, fontSize = 12.sp, lineHeight = 17.sp)
            }
        }
    }
}

@Composable
fun ModelRow(name: String, tech: String, size: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardSurface)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(name, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(tech, color = TextMuted, fontSize = 11.sp)
        }
        Text(size, color = accent, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun LatencyBars(accent: Color) {
    val bars = listOf(
        "TFLite" to 0.15f,
        "ML Kit" to 0.20f,
        "MediaPipe" to 0.25f,
        "Gemini Nano" to 0.90f,
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(CardSurface)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        bars.forEach { (label, fraction) ->
            val anim by animateFloatAsState(
                targetValue = fraction,
                animationSpec = tween(1200, easing = EaseOut),
                label = "lat$label"
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    label, color = TextPrimary, fontSize = 12.sp,
                    modifier = Modifier.width(90.dp)
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CardBorder)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(anim)
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(if (fraction > 0.5f) AccentCoral else accent)
                    )
                }
                Spacer(Modifier.width(8.dp))
                Text(
                    "${(fraction * 500).toInt()}ms",
                    color = TextMuted, fontSize = 11.sp,
                    modifier = Modifier.width(40.dp),
                    textAlign = TextAlign.End
                )
            }
        }
    }
}
