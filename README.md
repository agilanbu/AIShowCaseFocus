# AI Showcase — Native Android (Jetpack Compose)

A single Android project demonstrating **5 on-device AI technologies**, each with its own screen and working implementation.

## 📱 Screens & Technologies

| Screen | Technology | Feature |
|---------|------------|----------|
| **Home** | Navigation | 5 button cards → each screen |
| **TFLite Screen** | TensorFlow Lite | MobileNet image classification |
| **Gemini Nano Screen** | Gemini Nano / AICore | On-device LLM chat (streaming) |
| **On-Device AI Screen** | On-Device AI concepts | Cloud vs Edge comparison dashboard |
| **ML Kit Screen** | Google ML Kit | Text recognition from Bitmap |
| **MediaPipe Screen** | MediaPipe Tasks | Animated 33-landmark pose detection |

---

## 🏗️ Project Structure

```plaintext
app/src/main/java/com/aishowcase/
├── MainActivity.kt
│   └── Entry point, sets up NavController
│
├── navigation/
│   └── NavGraph.kt
│       └── All routes defined here
│
├── theme/
│   └── Theme.kt
│       └── Dark theme, brand colors
│
└── ui/
    ├── home/
    │   ├── HomeScreen.kt
    │   │   └── 5 feature cards with navigation
    │   └── SharedComponents.kt
    │       └── AITopBar, ConceptCard, CodeBlock, StepRow
    │
    ├── tflite/
    │   └── TFLiteScreen.kt
    │       └── TFLite interpreter + result bars
    │
    ├── gemini/
    │   └── GeminiNanoScreen.kt
    │       └── Streaming chat UI
    │
    ├── ondevice/
    │   └── OnDeviceAIScreen.kt
    │       └── Educational dashboard
    │
    ├── mlkit/
    │   └── MLKitScreen.kt
    │       └── Text recognition demo
    │
    └── mediapipe/
        └── MediaPipeScreen.kt
            └── Animated pose skeleton canvas
```

---

## ✨ Features

- **TensorFlow Lite** → MobileNet image classification
- **Gemini Nano / AICore** → On-device LLM streaming chat
- **Google ML Kit** → OCR text recognition from images
- **MediaPipe Tasks** → Real-time pose landmark detection
- **Jetpack Compose Navigation** → Clean multi-screen architecture
- **Modern UI** → Dark theme with reusable components

---

## 🛠️ Tech Stack

- **Kotlin**
- **Jetpack Compose**
- **TensorFlow Lite**
- **Gemini Nano / AI Core**
- **Google ML Kit**
- **MediaPipe Tasks**
- **Navigation Compose**
- **Material 3**

---

## 📌 Architecture

The app follows a **modular screen-based structure** using **Jetpack Compose Navigation**, where each AI technology is isolated into its own package for clarity and scalability.
