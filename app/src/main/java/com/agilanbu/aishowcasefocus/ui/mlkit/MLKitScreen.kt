package com.agilanbu.aishowcasefocus.ui.mlkit

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.agilanbu.aishowcasefocus.theme.*
import com.agilanbu.aishowcasefocus.ui.home.AITopBar
import com.agilanbu.aishowcasefocus.ui.home.CodeBlock
import com.agilanbu.aishowcasefocus.ui.home.ConceptCard
import com.agilanbu.aishowcasefocus.ui.home.SectionLabel
import com.agilanbu.aishowcasefocus.ui.home.StepRow
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import com.google.mlkit.vision.text.Text as VisionText
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
//import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.tasks.await

// ── ViewModel ──────────────────────────────────────────────────────────────
class MLKitViewModel : ViewModel() {
    private val _extractedText = MutableStateFlow("")
    val extractedText: StateFlow<String> = _extractedText

    private val _isProcessing  = MutableStateFlow(false)
    val isProcessing: StateFlow<Boolean> = _isProcessing

    private val _status        = MutableStateFlow("Tap 'Scan Text' to run ML Kit recognition")
    val status: StateFlow<String> = _status

    private val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)

    /**
     * Real ML Kit usage (works with any Bitmap from Camera or Gallery):
     *
     *   val image = InputImage.fromBitmap(bitmap, 0)
     *   val result: Text = recognizer.process(image).await()
     *   result.textBlocks.forEach { block -> log(block.text) }
     *
     * For CameraX live stream use ImageAnalysis.Analyzer:
     *   fun analyze(imageProxy: ImageProxy) {
     *       val image = InputImage.fromMediaImage(imageProxy.image!!, imageProxy.imageInfo.rotationDegrees)
     *       recognizer.process(image).addOnSuccessListener { ... }.addOnCompleteListener { imageProxy.close() }
     *   }
     */
    fun scanDemoBitmap() {
        viewModelScope.launch {
            _isProcessing.value = true
            _status.value = "Creating sample bitmap…"
            delay(400)

            try {
                val bitmap = createSampleTextBitmap()
                _status.value = "Running ML Kit TextRecognizer…"
                val image   = InputImage.fromBitmap(bitmap, 0)
                val result  = recognizer.process(image).await()
                _extractedText.value = if (result.text.isNotBlank()) result.text
                else demoText() // fallback if bitmap text is unclear
                _status.value = "✓ ML Kit extracted ${result.textBlocks.size} text block(s)"
            } catch (e: Exception) {
                // Fallback demo text
                _extractedText.value = demoText()
                _status.value = "Demo output — connect a real camera for live recognition"
            }
            _isProcessing.value = false
        }
    }

    /** Programmatically draws text onto a Bitmap for ML Kit to process. */
    private fun createSampleTextBitmap(): Bitmap {
        val bmp    = Bitmap.createBitmap(600, 200, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bmp)
        canvas.drawColor(Color.WHITE)
        val paint  = Paint().apply {
            color    = Color.BLACK
            textSize = 48f
            isAntiAlias = true
        }
        canvas.drawText("AI Showcase",    40f, 80f,  paint)
        canvas.drawText("ML Kit Rocks!",  40f, 160f, paint)
        return bmp
    }

    private fun demoText() = "AI Showcase\nML Kit Rocks!\n\n[Add real camera input for live text scanning from your environment]"
}

// ── Screen ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MLKitScreen(navController: NavController, vm: MLKitViewModel = viewModel()) {
    val extractedText by vm.extractedText.collectAsState()
    val isProcessing  by vm.isProcessing.collectAsState()
    val status        by vm.status.collectAsState()
    val context       = LocalContext.current
    val accent        = AccentCoral

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .systemBarsPadding()
    ) {
        AITopBar(title = "Google ML Kit", accent = accent, navController = navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            ConceptCard(
                emoji = "📄",
                title = "What is Google ML Kit?",
                body  = "ML Kit is Google's mobile SDK that brings powerful ML capabilities — text recognition, face detection, barcode scanning, translation, and more — directly to Android apps, fully on-device.",
                accent = accent
            )

            Spacer(Modifier.height(20.dp))

            SectionLabel("HOW TEXT RECOGNITION WORKS")
            StepRow(steps = listOf(
                "Camera → Bitmap captured",
                "InputImage.fromBitmap(bitmap, rotation)",
                "recognizer.process(image).await()",
                "TextBlock → lines → elements extracted"
            ), accent = accent)

            Spacer(Modifier.height(24.dp))

            // ── Demo image preview ─────────────────────────────────────────
            SectionLabel("SAMPLE INPUT IMAGE")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(androidx.compose.ui.graphics.Color.White.copy(alpha = 0.95f)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "AI Showcase",
                        color = androidx.compose.ui.graphics.Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                    Text(
                        text = "ML Kit Rocks!",
                        color = androidx.compose.ui.graphics.Color.Black,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        fontFamily = FontFamily.Serif
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = status,
                color = if (isProcessing) accent else TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { vm.scanDemoBitmap() },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isProcessing,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                if (isProcessing) {
                    CircularProgressIndicator(
                        color = androidx.compose.ui.graphics.Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("🔍  Scan Text with ML Kit", fontWeight = FontWeight.Bold,
                        color = androidx.compose.ui.graphics.Color.White)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Extracted text output ──────────────────────────────────────
            if (extractedText.isNotBlank()) {
                SectionLabel("EXTRACTED TEXT")
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(14.dp))
                        .background(CardSurface)
                        .border(1.dp, accent.copy(alpha = 0.4f), RoundedCornerShape(14.dp))
                        .padding(16.dp)
                ) {
                    Column {
                        Text(
                            text = extractedText,
                            color = TextPrimary,
                            fontSize = 14.sp,
                            lineHeight = 22.sp,
                            fontFamily = FontFamily.Monospace
                        )
                        Spacer(Modifier.height(12.dp))
                        TextButton(
                            onClick = {
                                val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                clipboard.setPrimaryClip(ClipData.newPlainText("ML Kit Text", extractedText))
                            }
                        ) {
                            Icon(Icons.Default.ContentCopy, contentDescription = "Copy",
                                tint = accent, modifier = Modifier.size(16.dp))
                            Spacer(Modifier.width(6.dp))
                            Text("Copy to clipboard", color = accent, fontSize = 13.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("KEY CODE")
            CodeBlock("""
val recognizer = TextRecognition.getClient(
    TextRecognizerOptions.DEFAULT_OPTIONS
)
val image  = InputImage.fromBitmap(bitmap, 0)
val result = recognizer.process(image).await()
result.textBlocks.forEach { block ->
    Log.d("MLKit", block.text)
}
            """.trimIndent(), accent)

            // ── ML Kit features list ───────────────────────────────────────
            Spacer(Modifier.height(24.dp))
            SectionLabel("OTHER ML KIT FEATURES")
            val features = listOf(
                "👤 Face Detection" to "Detect faces, landmarks, expressions",
                "📊 Barcode Scanning" to "QR codes, UPC, Data Matrix",
                "🌍 Translation" to "Translate text on-device (50+ languages)",
                "🏷️ Image Labeling" to "Label objects in photos",
                "🤟 Pose Detection" to "Body landmark detection (also in MediaPipe)"
            )
            features.forEach { (title, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(CardSurface)
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.width(140.dp))
                    Text(desc, color = TextMuted, fontSize = 12.sp, lineHeight = 17.sp)
                }
            }

            Spacer(Modifier.height(32.dp))
        }
    }
}
