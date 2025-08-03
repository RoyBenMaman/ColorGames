package com.roy.color

import android.os.Bundle
import android.speech.tts.TextToSpeech
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.util.*

class MainActivity : ComponentActivity(), TextToSpeech.OnInitListener {

    private var tts: TextToSpeech? = null
    private var isTtsReady by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tts = TextToSpeech(this, this)

        setContent {
            ColorQuizWithSpeechAndRepeat(tts, isTtsReady)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        tts?.stop()
        tts?.shutdown()
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            tts?.language = Locale.US
            isTtsReady = true
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ColorQuizWithSpeechAndRepeat(tts: TextToSpeech?, isTtsReady: Boolean) {
    val colorNames = listOf(
        "Red", "Blue", "Green", "Yellow", "Purple",
        "Orange", "Pink", "Brown", "Gray", "Cyan",
        "Magenta", "Teal", "Indigo", "Olive"  // Added Indigo and Olive
    )

    val colorMap = mapOf(
        "Red" to Color.Red,
        "Blue" to Color.Blue,
        "Green" to Color.Green,
        "Yellow" to Color.Yellow,
        "Purple" to Color(0xFF800080),
        "Orange" to Color(0xFFFFA500),
        "Pink" to Color(0xFFFFC0CB),
        "Brown" to Color(0xFFA52A2A),
        "Gray" to Color.Gray,
        "Cyan" to Color.Cyan,
        "Magenta" to Color.Magenta,
        "Teal" to Color(0xFF008080),
        "Indigo" to Color(0xFF4B0082),  // Added Indigo
        "Olive" to Color(0xFF808000)    // Added Olive
    )

    var currentColor by remember { mutableStateOf(colorNames.random()) }
    var colorRepeatVersion by remember { mutableStateOf(0) }
    var errorCount by remember { mutableStateOf(0) }
    var repeatRequested by remember { mutableStateOf(false) }
    var backgroundColor by remember { mutableStateOf(Color.White) }
    val coroutineScope = rememberCoroutineScope()

    // מקריא את הצבע לאחר שה-TTS מוכן
    LaunchedEffect(isTtsReady) {
        if (isTtsReady) {
            delay(300L)
            tts?.speak(currentColor, TextToSpeech.QUEUE_FLUSH, null, "color_start")
        }
    }

    LaunchedEffect(currentColor, colorRepeatVersion, isTtsReady) {
        if (isTtsReady) {
            delay(200L)
            tts?.speak(currentColor, TextToSpeech.QUEUE_FLUSH, null, "color")
        }
    }

    LaunchedEffect(repeatRequested, isTtsReady) {
        if (repeatRequested && isTtsReady) {
            delay(200L)
            tts?.speak(currentColor, TextToSpeech.QUEUE_FLUSH, null, "color_repeat")
            repeatRequested = false
        }
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = backgroundColor
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("🎨 What color is it?", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(12.dp))
            Text("Mistakes: $errorCount", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { repeatRequested = true },
                colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)
            ) {
                Text("🔁 Repeat")
            }

            Spacer(modifier = Modifier.height(24.dp))

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(colorNames) { colorName ->
                    Button(
                        onClick = {
                            if (colorName == currentColor) {
                                tts?.speak("Correct!", TextToSpeech.QUEUE_FLUSH, null, "correct")
                                backgroundColor = Color(0xFFDFFFD6)
                                coroutineScope.launch {
                                    delay(800L)
                                    backgroundColor = Color.White
                                    currentColor = colorNames.random()
                                    colorRepeatVersion++ // משנה גם אם חוזר
                                    errorCount = 0
                                }
                            } else {
                                tts?.speak("Try again", TextToSpeech.QUEUE_FLUSH, null, "wrong")
                                backgroundColor = Color(0xFFFFD6D6)
                                errorCount++
                                coroutineScope.launch {
                                    delay(600L)
                                    backgroundColor = Color.White
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = colorMap[colorName] ?: Color.LightGray),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                    ) {
                        Text(colorName, color = Color.White)
                    }
                }
            }
        }
    }
}