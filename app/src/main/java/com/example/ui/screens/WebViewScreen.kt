package com.example.ui.screens

import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.ads.AdManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun WebViewScreen(url: String, onBackPress: () -> Unit) {
    BackHandler {
        // Show ad on back press, then navigate back
        AdManager.showInterstitial {
            onBackPress()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        // The embedded web site
        AndroidView(
            factory = { context ->
                WebView(context).apply {
                    settings.javaScriptEnabled = true
                    settings.domStorageEnabled = true
                    webViewClient = WebViewClient()
                    loadUrl(url)
                }
            },
            modifier = Modifier.weight(1f)
        )

        // Banner Ad at the bottom
        if (AdManager.adsEnabled) {
            SimulatedBannerAd()
        }
    }
}

@Composable
fun SimulatedBannerAd() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(Color.DarkGray),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Advertisement (Banner) - StartApp / Monetag",
            color = Color.White,
            fontSize = 12.sp
        )
    }
}

@Composable
fun SimulatedInterstitialAdOverlays(
    isVisible: Boolean,
    onClose: () -> Unit
) {
    if (isVisible) {
        var skippable by remember { mutableStateOf(false) }
        var timeLeft by remember { mutableStateOf(5) }
        
        LaunchedEffect(Unit) {
            timeLeft = 5
            skippable = false
            while (timeLeft > 0) {
                delay(1000)
                timeLeft--
            }
            skippable = true
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "This is a Simulated Video Ad",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Replace with Start.io/Monetag later",
                    color = Color.Gray
                )
            }

            // Close button
            if (skippable) {
                IconButton(
                    onClick = {
                        onClose()
                        AdManager.closeInterstitial()
                    },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(32.dp)
                        .background(Color.White.copy(alpha = 0.2f), shape = MaterialTheme.shapes.small)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close Ad", tint = Color.White)
                }
            } else {
                Text(
                    text = "Skip in $timeLeft",
                    color = Color.White,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(32.dp)
                )
            }
        }
    }
}
