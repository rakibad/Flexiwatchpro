package com.example.ads

import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * A Simulated Ad Manager that demonstrates the ad flow described by the user.
 * In a real-world scenario, you would replace `showInterstitial` with 
 * StartAppAd.showAd(context) or monetag equivalents.
 */
object AdManager {
    
    private val _isShowingInterstitial = MutableStateFlow(false)
    val isShowingInterstitial: StateFlow<Boolean> = _isShowingInterstitial.asStateFlow()
    
    var adsEnabled = true

    fun showInterstitial(onAdClosed: () -> Unit = {}) {
        if (!adsEnabled) {
            onAdClosed()
            return
        }
        
        // Show simulated interstitial
        _isShowingInterstitial.value = true
        
        // In real SDK callback:
        // StartAppAd.showAd(context, object : AdDisplayListener {
        //     override fun adHidden(ad: Ad) { onAdClosed() } ...
        // })
    }
    
    fun closeInterstitial() {
        _isShowingInterstitial.value = false
    }
}
