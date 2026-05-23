package com.agilanbu.aishowcasefocus.ui.gemini

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.aishowcase.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

// ── Data ───────────────────────────────────────────────────────────────────
data class ChatMessage(val role: String, val text: String) // role: "user" | "model"

// ── ViewModel ──────────────────────────────────────────────────────────────
class GeminiViewModel : ViewModel() {
    private val _messages   = MutableStateFlow<List<ChatMessage>>(
        listOf(ChatMessage("model", "Hi! I'm Gemini Nano running on your device. Ask me anything 🤖"))
    )
    val messages: StateFlow<List<ChatMessage>> = _messages

    private val _isThinking = MutableStateFlow(false)
    val isThinking: StateFlow<Boolean> = _isThinking

    /**
     * In a real implementation using AICore (Pixel 9+ / Android 15):
     *
     *   val model = GenerativeModel(modelName = "gemini-nano")
     *   val response = model.generateContentStream(prompt)
     *   response.collect { chunk -> appendToken(chunk.text ?: "") }
     *
     * AICore requires the Gemini Nano model to be pre-downloaded on-device.
     * Check availability with: GenerativeModel.isAvailable(context)
     *
     * For devices without AICore, fall back to Gemini API with an API key:
     *   val model = GenerativeModel(modelName = "gemini-1.5-flash", apiKey = BuildConfig.GEMINI_API_KEY)
     *
     * Here we simulate streaming output so the UI logic is identical.
     */
    fun sendMessage(userText: String) {
        if (userText.isBlank()) return
        viewModelScope.launch {
            // Add user message
            _messages.value = _messages.value + ChatMessage("user", userText)
            _isThinking.value = true

            delay(800) // simulate processing

            // Simulate streamed response
            val reply = generateDemoReply(userText)
            val streamed = StringBuilder()
            // Add empty model message first
            _messages.value = _messages.value + ChatMessage("model", "")
            for (char in reply) {
                streamed.append(char)
                // Update the last message in-place
                val updated = _messages.value.toMutableList()
                updated[updated.lastIndex] = ChatMessage("model", streamed.toString())
                _messages.value = updated
                delay(18) // token stream delay
            }
            _isThinking.value = false
        }
    }

    private fun generateDemoReply(input: String): String {
        val lower = input.lowercase()
        return when {
            "hello" in lower || "hi" in lower ->
                "Hello! Running fully on-device, no internet needed. What would you like to know?"
            "what" in lower && "gemini nano" in lower ->
                "Gemini Nano is Google's smallest LLM, optimized to run directly on Android phones via AICore. It handles tasks like summarization, Q&A, and smart replies without sending data to the cloud."
            "how" in lower && ("work" in lower || "run" in lower) ->
                "On Pixel 9+ with Android 15, AICore manages Gemini Nano as a shared model. Apps call the GenerativeAI SDK — the model never leaves your device. Data stays private."
            "privacy" in lower ->
                "Since Gemini Nano runs on-device, your prompts and responses never leave your phone. No API calls, no server logs. True local inference."
            "compose" in lower || "android" in lower ->
                "This entire app is built with Jetpack Compose. The UI observes StateFlow from ViewModels using collectAsState() — clean, reactive, and lifecycle-aware."
            else ->
                "That's a great question! In a real deployment I'd use GenerativeModel(\"gemini-nano\") with AICore to stream a real answer on-device. For now, this demo simulates the response stream so you can see exactly how the UI would behave."
        }
    }
}

// ── Screen ─────────────────────────────────────────────────────────────────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GeminiNanoScreen(navController: NavController, vm: GeminiViewModel = viewModel()) {
    val messages   by vm.messages.collectAsState()
    val isThinking by vm.isThinking.collectAsState()
    var inputText  by remember { mutableStateOf("") }
    val listState  = rememberLazyListState()
    val accent     = NeonGreen

    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) listState.animateScrollToItem(messages.lastIndex)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkSurface)
            .systemBarsPadding()
    ) {
        AITopBar(title = "Gemini Nano", accent = accent, navController = navController)

        // ── Concept strip ──────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSurface)
                .padding(horizontal = 20.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("🔒", fontSize = 14.sp)
            Spacer(Modifier.width(8.dp))
            Text(
                "Fully on-device · No API key · No internet required",
                color = TextMuted, fontSize = 11.sp
            )
        }

        // ── Chat list ──────────────────────────────────────────────────────
        LazyColumn(
            state = listState,
            modifier = Modifier.weight(1f).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 16.dp)
        ) {
            items(messages) { msg ->
                ChatBubble(message = msg, accent = accent)
            }
            if (isThinking) {
                item { ThinkingBubble(accent) }
            }
        }

        // ── Suggestion chips ───────────────────────────────────────────────
        val suggestions = listOf("What is Gemini Nano?", "How does it work?", "Is it private?")
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            suggestions.forEach { s ->
                SuggestionChip(
                    onClick = { vm.sendMessage(s) },
                    label = { Text(s, fontSize = 11.sp) },
                    shape = RoundedCornerShape(20.dp),
                    colors = SuggestionChipDefaults.suggestionChipColors(
                        containerColor = CardSurface,
                        labelColor = TextMuted
                    ),
                    border = SuggestionChipDefaults.suggestionChipBorder(
                        enabled = true,
                        borderColor = CardBorder
                    ),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // ── Input bar ──────────────────────────────────────────────────────
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = inputText,
                onValueChange = { inputText = it },
                placeholder = { Text("Ask Gemini Nano…", color = TextMuted, fontSize = 14.sp) },
                modifier = Modifier.weight(1f),
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor   = accent,
                    unfocusedBorderColor = CardBorder,
                    focusedTextColor     = TextPrimary,
                    unfocusedTextColor   = TextPrimary,
                    cursorColor          = accent,
                    focusedContainerColor   = DarkSurface,
                    unfocusedContainerColor = DarkSurface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    vm.sendMessage(inputText); inputText = ""
                }),
                singleLine = true
            )
            Spacer(Modifier.width(10.dp))
            IconButton(
                onClick = { vm.sendMessage(inputText); inputText = "" },
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(24.dp))
                    .background(accent)
            ) {
                Icon(Icons.Default.Send, contentDescription = "Send", tint = Color.Black)
            }
        }
    }
}

// ── Chat bubble ────────────────────────────────────────────────────────────
@Composable
fun ChatBubble(message: ChatMessage, accent: Color) {
    val isUser = message.role == "user"
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(accent.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) { Text("G", color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
            Spacer(Modifier.width(8.dp))
        }
        Box(
            modifier = Modifier
                .widthIn(max = 280.dp)
                .clip(
                    RoundedCornerShape(
                        topStart    = if (isUser) 16.dp else 4.dp,
                        topEnd      = 16.dp,
                        bottomStart = 16.dp,
                        bottomEnd   = if (isUser) 4.dp else 16.dp
                    )
                )
                .background(if (isUser) accent.copy(alpha = 0.2f) else CardSurface)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Text(
                text = message.text,
                color = if (isUser) accent else TextPrimary,
                fontSize = 14.sp,
                lineHeight = 21.sp
            )
        }
    }
}

@Composable
fun ThinkingBubble(accent: Color) {
    val infiniteTransition = rememberInfiniteTransition(label = "dots")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.3f, targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(600), RepeatMode.Reverse),
        label = "dotAlpha"
    )
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(accent.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) { Text("G", color = accent, fontWeight = FontWeight.Bold, fontSize = 14.sp) }
        Spacer(Modifier.width(8.dp))
        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(16.dp))
                .background(CardSurface)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Text("Thinking…", color = accent.copy(alpha = alpha), fontSize = 14.sp)
        }
    }
}
