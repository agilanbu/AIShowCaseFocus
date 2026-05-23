package com.agilanbu.aishowcasefocus.ui.home

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.aishowcase.navigation.Routes
import com.aishowcase.theme.*
import kotlinx.coroutines.delay

// ── Data model for each AI feature card ───────────────────────────────────
data class AIFeature(
    val title: String,
    val subtitle: String,
    val description: String,
    val accentColor: Color,
    val emoji: String,
    val route: String
)

val aiFeatures = listOf(
    AIFeature(
        title       = "TensorFlow Lite",
        subtitle    = "Image Classification",
        description = "Run a MobileNet model on-device to classify images in real time. No internet required.",
        accentColor = AccentBlue,
        emoji       = "🧠",
        route       = Routes.TFLITE
    ),
    AIFeature(
        title       = "Gemini Nano",
        subtitle    = "On-Device Chat",
        description = "Chat with Google's Gemini Nano LLM running fully on your Android device via AICore.",
        accentColor = NeonGreen,
        emoji       = "💬",
        route       = Routes.GEMINI
    ),
    AIFeature(
        title       = "On-Device AI",
        subtitle    = "Privacy-First Intelligence",
        description = "Understand why running AI on-device matters — zero data leaves your phone.",
        accentColor = AccentAmber,
        emoji       = "🔒",
        route       = Routes.ON_DEVICE
    ),
    AIFeature(
        title       = "Google ML Kit",
        subtitle    = "Text Recognition",
        description = "Point your camera at any text. ML Kit extracts and translates it instantly, offline.",
        accentColor = AccentCoral,
        emoji       = "📄",
        route       = Routes.ML_KIT
    ),
    AIFeature(
        title       = "MediaPipe",
        subtitle    = "Pose Detection",
        description = "Detect 33 body landmarks live from your camera using MediaPipe's Pose Landmarker.",
        accentColor = AccentPurple,
        emoji       = "🏃",
        route       = Routes.MEDIAPIPE
    )
)

// ── Home Screen ────────────────────────────────────────────────────────────
@Composable
fun HomeScreen(navController: NavController) {
    // Animated pulse for the hero dot
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    // Staggered entrance for cards
    val cardVisibles = remember { List(aiFeatures.size) { mutableStateOf(false) } }
    LaunchedEffect(Unit) {
        cardVisibles.forEachIndexed { i, state ->
            delay(150L * i)
            state.value = true
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
    ) {
        // Subtle radial glow behind hero text
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
                .drawBehind {
                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(NeonGreen.copy(alpha = 0.08f), Color.Transparent),
                            center = Offset(size.width / 2, size.height * 0.4f),
                            radius = size.width * 0.7f
                        )
                    )
                }
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .systemBarsPadding()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(32.dp))

            // ── Live indicator ─────────────────────────────────────────────
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .background(CardSurface, RoundedCornerShape(20.dp))
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .alpha(pulseAlpha)
                        .background(NeonGreen, shape = RoundedCornerShape(4.dp))
                )
                Spacer(Modifier.width(8.dp))
                Text(
                    text = "AI running on-device",
                    color = TextMuted,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Spacer(Modifier.height(28.dp))

            // ── Hero title ─────────────────────────────────────────────────
            Text(
                text = "Native AI\non Android",
                color = TextPrimary,
                fontSize = 38.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Default,
                textAlign = TextAlign.Center,
                lineHeight = 44.sp
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Explore 5 on-device AI technologies,\nall running locally. No cloud. No latency.",
                color = TextMuted,
                fontSize = 14.sp,
                textAlign = TextAlign.Center,
                lineHeight = 22.sp
            )

            Spacer(Modifier.height(40.dp))

            // ── Section label ──────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .width(3.dp)
                        .height(16.dp)
                        .background(NeonGreen, RoundedCornerShape(2.dp))
                )
                Spacer(Modifier.width(10.dp))
                Text(
                    text = "CHOOSE A TECHNOLOGY",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.5.sp
                )
            }

            Spacer(Modifier.height(16.dp))

            // ── Feature cards ──────────────────────────────────────────────
            aiFeatures.forEachIndexed { index, feature ->
                val visible by cardVisibles[index]
                val alpha by animateFloatAsState(
                    targetValue = if (visible) 1f else 0f,
                    animationSpec = tween(400),
                    label = "cardAlpha$index"
                )
                val offsetY by animateDpAsState(
                    targetValue = if (visible) 0.dp else 20.dp,
                    animationSpec = tween(400, easing = EaseOut),
                    label = "cardOffset$index"
                )

                Box(modifier = Modifier.offset(y = offsetY).alpha(alpha)) {
                    AIFeatureCard(
                        feature = feature,
                        onClick = { navController.navigate(feature.route) }
                    )
                }

                Spacer(Modifier.height(12.dp))
            }

            Spacer(Modifier.height(32.dp))

            // ── Footer note ────────────────────────────────────────────────
            Text(
                text = "Built with Jetpack Compose · All AI runs on-device",
                color = TextMuted.copy(alpha = 0.5f),
                fontSize = 11.sp,
                textAlign = TextAlign.Center
            )

            Spacer(Modifier.height(24.dp))
        }
    }
}

// ── Individual feature card ────────────────────────────────────────────────
@Composable
fun AIFeatureCard(feature: AIFeature, onClick: () -> Unit) {
    OutlinedButton(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        border = BorderStroke(1.dp, feature.accentColor.copy(alpha = 0.35f)),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = CardSurface,
            contentColor   = TextPrimary
        ),
        contentPadding = PaddingValues(0.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Emoji icon box
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .background(
                        feature.accentColor.copy(alpha = 0.12f),
                        RoundedCornerShape(12.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                Text(feature.emoji, fontSize = 24.sp)
            }

            Spacer(Modifier.width(16.dp))

            // Text block
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = feature.title,
                    color = feature.accentColor,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = feature.subtitle,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = feature.description,
                    color = TextMuted,
                    fontSize = 12.sp,
                    lineHeight = 17.sp
                )
            }

            Spacer(Modifier.width(8.dp))

            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Open ${feature.title}",
                tint = feature.accentColor.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
