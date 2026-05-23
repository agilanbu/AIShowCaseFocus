package com.agilanbu.aishowcasefocus.navigation


import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.aishowcase.ui.gemini.GeminiNanoScreen
import com.aishowcase.ui.home.HomeScreen
import com.aishowcase.ui.mediapipe.MediaPipeScreen
import com.aishowcase.ui.mlkit.MLKitScreen
import com.aishowcase.ui.ondevice.OnDeviceAIScreen
import com.aishowcase.ui.tflite.TFLiteScreen

// ── Route constants ────────────────────────────────────────────────────────
object Routes {
    const val HOME       = "home"
    const val TFLITE     = "tflite"
    const val GEMINI     = "gemini"
    const val ON_DEVICE  = "on_device"
    const val ML_KIT     = "ml_kit"
    const val MEDIAPIPE  = "mediapipe"
}

@Composable
fun AINavGraph(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Routes.HOME
    ) {
        composable(Routes.HOME)      { HomeScreen(navController) }
        composable(Routes.TFLITE)    { TFLiteScreen(navController) }
        composable(Routes.GEMINI)    { GeminiNanoScreen(navController) }
        composable(Routes.ON_DEVICE) { OnDeviceAIScreen(navController) }
        composable(Routes.ML_KIT)    { MLKitScreen(navController) }
        composable(Routes.MEDIAPIPE) { MediaPipeScreen(navController) }
    }
}
