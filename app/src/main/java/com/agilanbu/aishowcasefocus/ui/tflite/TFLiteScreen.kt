package com.agilanbu.aishowcasefocus.ui.tflite

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aishowcase.theme.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import org.tensorflow.lite.Interpreter
import java.io.FileInputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.channels.FileChannel

// ── ViewModel ──────────────────────────────────────────────────────────────
data class ClassificationResult(val label: String, val confidence: Float)

class TFLiteViewModel : ViewModel() {
    private val _results   = MutableStateFlow<List<ClassificationResult>>(emptyList())
    val results: StateFlow<List<ClassificationResult>> = _results

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _status    = MutableStateFlow("Ready to classify")
    val status: StateFlow<String> = _status

    /**
     * Loads a TFLite model from assets and runs inference on a demo bitmap.
     *
     * In a real app:
     *   1. Place mobilenet_v1_1.0_224_quant.tflite in app/src/main/assets/
     *   2. Replace demoLabels below with ImageNet labels loaded from assets/labels.txt
     *   3. Run classifyBitmap() on a real camera Bitmap
     *
     * Here we simulate the pipeline so the screen works without the model binary.
     */
    fun runClassification(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.value = true
            _status.value    = "Loading TFLite model from assets…"
            delay(600)

            try {
                // ── Attempt to load real model ──────────────────────────
                val model = loadModelFromAssets(context, "mobilenet_v1_1.0_224_quant.tflite")
                _status.value = "Running MobileNet inference…"
                delay(400)

                val interpreter = Interpreter(model)
                // Input: [1, 224, 224, 3] UINT8
                val inputBuffer  = ByteBuffer.allocateDirect(1 * 224 * 224 * 3)
                    .apply { order(ByteOrder.nativeOrder()); rewind() }
                // Output: [1, 1001] UINT8 scores
                val outputArray  = Array(1) { ByteArray(1001) }
                interpreter.run(inputBuffer, outputArray)

                // Decode top-3 (without real labels file, show index)
                val scores = outputArray[0].mapIndexed { i, b -> i to (b.toInt() and 0xFF) }
                val top3   = scores.sortedByDescending { it.second }.take(3)
                _results.value = top3.map {
                    ClassificationResult("Class index ${it.first}", it.second / 255f)
                }
                _status.value = "Inference complete ✓"
            } catch (e: Exception) {
                // ── Simulated results (no model file in assets) ─────────
                _status.value = "Simulating MobileNet output…"
                delay(500)
                _results.value = listOf(
                    ClassificationResult("Golden Retriever 🐶", 0.94f),
                    ClassificationResult("Labrador Retriever", 0.04f),
                    ClassificationResult("Cocker Spaniel",     0.01f)
                )
                _status.value = "Demo mode — add mobilenet_v1_1.0_224_quant.tflite to assets/ for live inference"
            }

            _isLoading.value = false
        }
    }

    private fun loadModelFromAssets(context: Context, fileName: String): ByteBuffer {
        val assetFd      = context.assets.openFd(fileName)
        val inputStream  = FileInputStream(assetFd.fileDescriptor)
        val fileChannel  = inputStream.channel
        val startOffset  = assetFd.startOffset
        val declaredLen  = assetFd.declaredLength
        return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLen)
    }
}

// ── Screen ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TFLiteScreen(navController: NavController, vm: TFLiteViewModel = viewModel()) {
    val results   by vm.results.collectAsState()
    val isLoading by vm.isLoading.collectAsState()
    val status    by vm.status.collectAsState()
    val context   = LocalContext.current

    val accent = AccentBlue

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .systemBarsPadding()
    ) {
        // ── Top bar ────────────────────────────────────────────────────────
        AITopBar(title = "TensorFlow Lite", accent = accent, navController = navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            // ── Concept card ───────────────────────────────────────────────
            ConceptCard(
                emoji = "🧠",
                title = "What is TensorFlow Lite?",
                body  = "TFLite is Google's ML framework for mobile. It converts trained neural network models (.tflite) into a compact format that runs fast on Android CPUs/GPUs — no internet required.",
                accent = accent
            )

            Spacer(Modifier.height(20.dp))

            // ── How it works ───────────────────────────────────────────────
            SectionLabel("HOW IT WORKS")
            StepRow(steps = listOf(
                "Load .tflite model from assets/",
                "Convert Bitmap → ByteBuffer",
                "interpreter.run(input, output)",
                "Decode output → label + score"
            ), accent = accent)

            Spacer(Modifier.height(24.dp))

            // ── Demo area ──────────────────────────────────────────────────
            SectionLabel("LIVE DEMO")
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(CardSurface)
                    .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(16.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("📷", fontSize = 40.sp)
                    Spacer(Modifier.height(8.dp))
                    Text("Sample image (224×224)", color = TextMuted, fontSize = 12.sp)
                    Text("MobileNet V1 — ImageNet 1001 classes", color = TextMuted, fontSize = 11.sp)
                }
            }

            Spacer(Modifier.height(16.dp))

            // Status
            Text(
                text = status,
                color = if (isLoading) accent else TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(Modifier.height(12.dp))

            // Run button
            Button(
                onClick = { vm.runClassification(context) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = !isLoading,
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(containerColor = accent)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        color = Color.White,
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("▶  Run Image Classification", fontWeight = FontWeight.Bold, color = Color.Black)
                }
            }

            Spacer(Modifier.height(20.dp))

            // ── Results ────────────────────────────────────────────────────
            if (results.isNotEmpty()) {
                SectionLabel("RESULTS")
                results.forEachIndexed { i, r ->
                    ResultBar(rank = i + 1, result = r, accent = accent)
                    Spacer(Modifier.height(10.dp))
                }
            }

            // ── Code snippet ───────────────────────────────────────────────
            Spacer(Modifier.height(24.dp))
            SectionLabel("KEY CODE")
            CodeBlock("""
val interpreter = Interpreter(loadModelFromAssets(ctx, "mobilenet.tflite"))
val input  = convertBitmapToByteBuffer(bitmap)  // [1, 224, 224, 3]
val output = Array(1) { FloatArray(1001) }
interpreter.run(input, output)
val top = output[0].argmax()  // index of highest score
            """.trimIndent(), accent)

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Result bar ─────────────────────────────────────────────────────────────
@Composable
fun ResultBar(rank: Int, result: ClassificationResult, accent: Color) {
    val animWidth by animateFloatAsState(
        targetValue = result.confidence,
        animationSpec = tween(800, easing = EaseOut),
        label = "bar$rank"
    )
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(CardSurface)
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "#$rank  ${result.label}",
                color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Medium
            )
            Text(
                text = "${(result.confidence * 100).toInt()}%",
                color = accent, fontSize = 13.sp, fontWeight = FontWeight.Bold
            )
        }
        Spacer(Modifier.height(6.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CardBorder)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(animWidth)
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(accent)
            )
        }
    }
}
