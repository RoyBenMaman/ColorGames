package com.roy.color

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import com.roy.color.MainActivity
import kotlinx.coroutines.delay

class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SplashScreen()
        }
    }
}

@Composable
fun SplashScreen() {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        delay(2500L)
        context.startActivity(Intent(context, MainActivity::class.java))
        (context as? ComponentActivity)?.finish()
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color(0xFFFDE68A)
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "🎨 Color Fun!",
                fontSize = 36.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF3B3B3B)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Learn colors in English",
                fontSize = 18.sp,
                color = Color(0xFF4B5563)
            )
        }
    }
}
