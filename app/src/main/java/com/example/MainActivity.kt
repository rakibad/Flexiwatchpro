package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ads.AdManager
import com.example.data.ConfigRepository
import com.example.models.AppConfig
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.SimulatedInterstitialAdOverlays
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.WebViewScreen
import com.example.ui.theme.MyApplicationTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                MainApp()
            }
        }
    }
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val scope = rememberCoroutineScope()
    val repo = remember { ConfigRepository() }
    var config by remember { mutableStateOf(AppConfig()) }
    var isLoading by remember { mutableStateOf(true) }
    var currentWebUrl by remember { mutableStateOf("") }
    val isShowingAd by AdManager.isShowingInterstitial.collectAsState()

    // Fetch details
    LaunchedEffect(Unit) {
        config = repo.fetchConfig()
        AdManager.adsEnabled = config.adsEnabled
        isLoading = false
    }

    // Interval based Auto-Ads
    LaunchedEffect(config.adsEnabled, config.adIntervalSeconds) {
        if (config.adsEnabled && config.adIntervalSeconds > 0) {
            while (true) {
                delay(config.adIntervalSeconds * 1000L)
                AdManager.showInterstitial()
            }
        }
    }

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(modifier = Modifier.padding(innerPadding).fillMaxSize()) {
            NavHost(navController = navController, startDestination = "splash") {
                composable("splash") {
                    SplashScreen {
                        if (!isLoading) {
                            navController.navigate("home") {
                                popUpTo("splash") { inclusive = true }
                            }
                        }
                    }
                }
                composable("home") {
                    HomeScreen(
                        config = config,
                        onButtonClick = { button ->
                            AdManager.showInterstitial {
                                currentWebUrl = button.targetUrl
                                navController.navigate("webview")
                            }
                        }
                    )
                }
                composable("webview") {
                    WebViewScreen(
                        url = currentWebUrl,
                        onBackPress = {
                            navController.popBackStack()
                        }
                    )
                }
            }

            // Global Ad Overlay
            SimulatedInterstitialAdOverlays(
                isVisible = isShowingAd,
                onClose = { AdManager.closeInterstitial() }
            )
        }
    }
}
