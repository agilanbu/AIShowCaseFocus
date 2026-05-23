package com.agilanbu.aishowcasefocus.ui.mediaPipe


import android.content.Context
import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
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
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.platform.LocalContext
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
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.PI

// ── Landmark data ──────────────────────────────────────────────────────────
data class Landmark(val x: Float, val y: Float, val visibility: Float = 1f)

// MediaPipe Pose — 33 landmarks (subset used for display)
// Indices: 0=nose, 11=leftShoulder, 12=rightShoulder, 13=leftElbow, 14=rightElbow,
//          15=leftWrist, 16=rightWrist, 23=leftHip, 24=rightHip,
//          25=leftKnee, 26=rightKnee, 27=leftAnkle, 28=rightAnkle
val POSE_CONNECTIONS = listOf(
    11 to 12, // shoulder
    11 to 13, 13 to 15, // left arm
    12 to 14, 14 to 16, // right arm
    11 to 23, 12 to 24, // torso sides
    23 to 24,           // hips
    23 to 25, 25 to 27, // left leg
    24 to 26, 26 to 28, // right leg
    0 to 11, 0 to 12    // head to shoulders
)

// ── ViewModel ──────────────────────────────────────────────────────────────
class MediaPipeViewModel : ViewModel() {
    private val _landmarks  = MutableStateFlow<List<Landmark>>(emptyList())
    val landmarks: StateFlow<List<Landmark>> = _landmarks

    private val _isRunning  = MutableStateFlow(false)
    val isRunning: StateFlow<Boolean> = _isRunning

    private val _fps        = MutableStateFlow(0)
    val fps: StateFlow<Int> = _fps

    private val _status     = MutableStateFlow("Tap 'Start Detection' to run MediaPipe Pose")
    val status: StateFlow<String> = _status

    /**
     * Real MediaPipe implementation:
     *
     * val options = PoseLandmarker.PoseLandmarkerOptions.builder()
     *     .setBaseOptions(BaseOptions.builder()
     *         .setModelAssetPath("pose_landmarker_lite.task")  // in assets/
     *         .build())
     *     .setRunningMode(RunningMode.LIVE_STREAM)
     *     .setResultListener { result, _ ->
     *         _landmarks.value = result.landmarks().first()
     *             .map { Landmark(it.x(), it.y(), it.visibility().get()) }
     *     }
     *     .build()
     * val landmarker = PoseLandmarker.createFromOptions(context, options)
     *
     * In ImageAnalysis.Analyzer:
     *   val mpImage = BitmapImageBuilder(imageProxy.toBitmap()).build()
     *   landmarker.detectAsync(mpImage, imageProxy.imageInfo.timestamp)
     *
     * Here we animate a synthetic skeleton so the UI is fully functional.
     */
    fun startDetection() {
        if (_isRunning.value) return
        _isRunning.value = true
        _status.value    = "MediaPipe Pose Landmarker running…"
        viewModelScope.launch {
            var frame = 0
            while (_isRunning.value) {
                _landmarks.value = generateAnimatedPose(frame)
                _fps.value       = 28 + (frame % 4) // simulate ~28-30 fps
                frame++
                delay(33) // ~30 fps
            }
        }
    }

    fun stopDetection() {
        _isRunning.value = false
        _status.value    = "Stopped. Tap 'Start Detection' to resume."
        _fps.value       = 0
    }

    /** Generates an animated stick-figure pose (0..1 normalized coords). */
    private fun generateAnimatedPose(frame: Int): List<Landmark> {
        val t     = frame * 0.05
        val swing = (sin(t) * 0.04f).toFloat()

        // 33 landmarks — we place the important 29 with anatomically correct positions
        // Coord system: 0,0 = top-left, 1,1 = bottom-right
        val map = mutableMapOf<Int, Landmark>()
        map[0]  = Landmark(0.50f, 0.10f)                          // nose
        map[1]  = Landmark(0.52f, 0.09f)                          // left eye inner
        map[2]  = Landmark(0.54f, 0.09f)                          // left eye
        map[3]  = Landmark(0.56f, 0.09f)                          // left eye outer
        map[4]  = Landmark(0.48f, 0.09f)                          // right eye inner
        map[5]  = Landmark(0.46f, 0.09f)                          // right eye
        map[6]  = Landmark(0.44f, 0.09f)                          // right eye outer
        map[11] = Landmark(0.38f + swing, 0.28f)                  // left shoulder
        map[12] = Landmark(0.62f + swing, 0.28f)                  // right shoulder
        map[13] = Landmark(0.28f + swing, 0.42f + swing)          // left elbow
        map[14] = Landmark(0.72f + swing, 0.42f - swing)          // right elbow
        map[15] = Landmark(0.22f + swing, 0.57f + swing * 2)      // left wrist
        map[16] = Landmark(0.78f + swing, 0.57f - swing * 2)      // right wrist
        map[23] = Landmark(0.40f, 0.55f)                          // left hip
        map[24] = Landmark(0.60f, 0.55f)                          // right hip
        map[25] = Landmark(0.38f - swing * 0.5f, 0.73f)           // left knee
        map[26] = Landmark(0.62f + swing * 0.5f, 0.73f)           // right knee
        map[27] = Landmark(0.36f + swing, 0.90f)                  // left ankle
        map[28] = Landmark(0.64f - swing, 0.90f)                  // right ankle

        // Fill remaining with invisible landmarks
        return (0 until 33).map { map[it] ?: Landmark(0.5f, 0.5f, 0f) }
    }
}

// ── Screen ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MediaPipeScreen(navController: NavController, vm: MediaPipeViewModel = viewModel()) {
    val landmarks by vm.landmarks.collectAsState()
    val isRunning by vm.isRunning.collectAsState()
    val fps       by vm.fps.collectAsState()
    val status    by vm.status.collectAsState()
    val accent    = AccentPurple

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .systemBarsPadding()
    ) {
        AITopBar(title = "MediaPipe", accent = accent, navController = navController)

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(20.dp))

            ConceptCard(
                emoji = "🏃",
                title = "What is MediaPipe?",
                body  = "MediaPipe is Google's cross-platform ML solution for live media processing. The Pose Landmarker detects 33 body landmarks in real-time from camera frames, running fully on-device.",
                accent = accent
            )

            Spacer(Modifier.height(20.dp))

            SectionLabel("HOW POSE DETECTION WORKS")
            StepRow(steps = listOf(
                "CameraX → ImageProxy each frame",
                "BitmapImageBuilder(bitmap).build()",
                "landmarker.detectAsync(image, timestamp)",
                "33 NormalizedLandmarks (x, y, z, visibility)",
                "Canvas draws skeleton overlay"
            ), accent = accent)

            Spacer(Modifier.height(20.dp))

            // ── Pose canvas ────────────────────────────────────────────────
            SectionLabel("LIVE POSE VISUALIZATION")

            // FPS badge
            if (isRunning) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(20.dp))
                        .background(accent.copy(alpha = 0.15f))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text("$fps FPS", color = accent, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
                Spacer(Modifier.height(8.dp))
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(340.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color(0xFF0A0A0A))
                    .border(1.dp, accent.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
            ) {
                if (landmarks.isEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("🏃", fontSize = 40.sp)
                        Spacer(Modifier.height(12.dp))
                        Text("Pose visualization will appear here", color = TextMuted, fontSize = 13.sp)
                        Text("Tap Start Detection below", color = TextMuted.copy(alpha = 0.6f), fontSize = 11.sp)
                    }
                } else {
                    PoseCanvas(landmarks = landmarks, accent = accent)
                }
            }

            Spacer(Modifier.height(12.dp))
            Text(status, color = TextMuted, fontSize = 12.sp)
            Spacer(Modifier.height(12.dp))

            // Start / Stop button
            Button(
                onClick = { if (isRunning) vm.stopDetection() else vm.startDetection() },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) AccentCoral else accent
                )
            ) {
                Text(
                    if (isRunning) "⏹  Stop Detection" else "▶  Start Detection",
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }

            Spacer(Modifier.height(24.dp))

            // ── 33 Landmarks table ─────────────────────────────────────────
            SectionLabel("KEY LANDMARKS (33 TOTAL)")
            val landmarkNames = mapOf(
                0 to "Nose", 11 to "Left Shoulder", 12 to "Right Shoulder",
                13 to "Left Elbow", 14 to "Right Elbow", 15 to "Left Wrist",
                16 to "Right Wrist", 23 to "Left Hip", 24 to "Right Hip",
                25 to "Left Knee", 26 to "Right Knee", 27 to "Left Ankle", 28 to "Right Ankle"
            )
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(CardSurface)
                    .padding(4.dp)
            ) {
                landmarkNames.entries.forEachIndexed { idx, (id, name) ->
                    val lm = landmarks.getOrNull(id)
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(if (idx % 2 == 0) CardSurface else DarkSurface)
                            .padding(horizontal = 14.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("#$id  $name", color = TextPrimary, fontSize = 12.sp, modifier = Modifier.weight(1f))
                        if (lm != null && lm.visibility > 0f) {
                            Text(
                                "x:%.2f  y:%.2f".format(lm.x, lm.y),
                                color = accent, fontSize = 11.sp,
                                fontFamily = androidx.compose.ui.text.font.FontFamily.Monospace
                            )
                        } else {
                            Text("—", color = TextMuted, fontSize = 11.sp)
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            SectionLabel("KEY CODE")
            CodeBlock("""
val landmarker = PoseLandmarker.createFromOptions(ctx,
  PoseLandmarkerOptions.builder()
    .setBaseOptions(BaseOptions.builder()
      .setModelAssetPath("pose_landmarker_lite.task")
      .build())
    .setRunningMode(RunningMode.LIVE_STREAM)
    .setResultListener { result, _ ->
      val lms = result.landmarks().first()
      drawSkeleton(lms)   // update Canvas
    }.build()
)
landmarker.detectAsync(mpImage, timestamp)
            """.trimIndent(), accent)

            Spacer(Modifier.height(32.dp))
        }
    }
}

// ── Pose Canvas ────────────────────────────────────────────────────────────
@Composable
fun PoseCanvas(landmarks: List<Landmark>, accent: Color) {
    Canvas(modifier = Modifier.fillMaxSize()) {
        val w = size.width
        val h = size.height

        // Draw connections
        POSE_CONNECTIONS.forEach { (startIdx, endIdx) ->
            val s = landmarks.getOrNull(startIdx)
            val e = landmarks.getOrNull(endIdx)
            if (s != null && e != null && s.visibility > 0.5f && e.visibility > 0.5f) {
                drawLine(
                    color       = accent.copy(alpha = 0.7f),
                    start       = Offset(s.x * w, s.y * h),
                    end         = Offset(e.x * w, e.y * h),
                    strokeWidth = 3f,
                    cap         = StrokeCap.Round
                )
            }
        }

        // Draw landmark dots
        landmarks.forEachIndexed { i, lm ->
            if (lm.visibility > 0.5f) {
                drawCircle(
                    color  = Color.White,
                    radius = 5f,
                    center = Offset(lm.x * w, lm.y * h)
                )
                drawCircle(
                    color  = accent,
                    radius = 3f,
                    center = Offset(lm.x * w, lm.y * h)
                )
            }
        }
    }
}
