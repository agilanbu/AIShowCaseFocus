package com.agilanbu.aishowcasefocus.ui.home

// ── Shared composables used by every feature screen ──────────────────────
// File: SharedComponents.kt  (kept in ui.home package for simplicity,
//       but in a real project move to ui.components)

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.agilanbu.aishowcasefocus.theme.*

// ── Top bar ────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AITopBar(title: String, accent: Color, navController: NavController) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(CardSurface)
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { navController.popBackStack() }) {
            Icon(
                Icons.Default.ArrowBack,
                contentDescription = "Back",
                tint = accent
            )
        }
        Text(
            text = title,
            color = accent,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.weight(1f)
        )
        // On-device badge
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(accent.copy(alpha = 0.12f))
                .padding(horizontal = 10.dp, vertical = 4.dp)
        ) {
            Text("📱 On-Device", color = accent, fontSize = 10.sp, fontWeight = FontWeight.Medium)
        }
        Spacer(Modifier.width(8.dp))
    }
}

// ── Concept card ───────────────────────────────────────────────────────────
@Composable
fun ConceptCard(emoji: String, title: String, body: String, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(CardSurface)
            .border(1.dp, accent.copy(alpha = 0.25f), RoundedCornerShape(16.dp))
            .padding(16.dp)
    ) {
        Text(emoji, fontSize = 28.sp, modifier = Modifier.padding(top = 2.dp, end = 14.dp))
        Column {
            Text(title, color = accent, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Spacer(Modifier.height(4.dp))
            Text(body, color = TextPrimary, fontSize = 13.sp, lineHeight = 20.sp)
        }
    }
}

// ── Section label ──────────────────────────────────────────────────────────
@Composable
fun SectionLabel(text: String) {
    Text(
        text = text,
        color = TextMuted,
        fontSize = 10.sp,
        fontWeight = FontWeight.SemiBold,
        letterSpacing = 1.5.sp,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

// ── Step row ───────────────────────────────────────────────────────────────
@Composable
fun StepRow(steps: List<String>, accent: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        steps.forEachIndexed { i, step ->
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(10.dp))
                    .background(CardSurface)
                    .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(10.dp))
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .widthIn(min = 100.dp, max = 150.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        "${i + 1}",
                        color = accent,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        step,
                        color = TextPrimary,
                        fontSize = 11.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 15.sp
                    )
                }
            }
            if (i < steps.lastIndex) {
                Text(
                    "→",
                    color = TextMuted,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    }
}

// ── Code block ────────────────────────────────────────────────────────────
@Composable
fun CodeBlock(code: String, accent: Color) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(Color(0xFF080808))
            .border(1.dp, accent.copy(alpha = 0.2f), RoundedCornerShape(12.dp))
            .padding(14.dp)
            .horizontalScroll(rememberScrollState())
    ) {
        Text(
            text = code,
            color = Color(0xFF88CCAA),
            fontSize = 12.sp,
            fontFamily = FontFamily.Monospace,
            lineHeight = 19.sp
        )
    }
}
