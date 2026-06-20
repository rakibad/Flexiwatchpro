package com.example.data

import android.util.Log
import com.example.models.AppButton
import com.example.models.AppConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class ConfigRepository {
    
    // Replace this with the URL of the published Google Sheet (Format: CSV)
    // Defaulting to a placeholder, or falls back to local default.
    private val sheetCsvUrl = "https://docs.google.com/spreadsheets/d/e/2PACX-xxxx/pub?output=csv"

    suspend fun fetchConfig(): AppConfig {
        return withContext(Dispatchers.IO) {
            try {
                // If you put your Google Sheet CSV URL above, it will fetch.
                // For demonstration, since we don't have the user's real sheet, 
                // we'll return a default config if the URL is dummy.
                if (sheetCsvUrl.contains("xxxx")) {
                    return@withContext AppConfig()
                }

                val url = URL(sheetCsvUrl)
                val connection = url.openConnection() as HttpURLConnection
                connection.connectTimeout = 5000
                connection.readTimeout = 5000
                
                if (connection.responseCode == 200) {
                    val csvData = connection.inputStream.bufferedReader().readText()
                    parseCsvToConfig(csvData)
                } else {
                    AppConfig()
                }
            } catch (e: Exception) {
                Log.e("ConfigRepository", "Failed to fetch config", e)
                AppConfig() // Fallback to default
            }
        }
    }

    private fun parseCsvToConfig(csv: String): AppConfig {
        var notice = "স্বাগতম!"
        var customAdUrl = ""
        var adsEnabled = true
        var adIntervalSeconds = 120
        val buttons = mutableListOf<AppButton>()

        val lines = csv.split("\n")
        for (line in lines) {
            val parts = line.split(",").map { it.trim() }
            if (parts.isEmpty() || parts.size < 2) continue

            when (parts[0].lowercase()) {
                "setting" -> {
                    val key = parts[1]
                    val value = parts.getOrNull(2) ?: ""
                    when (key) {
                        "notice" -> notice = value
                        "custom_ad" -> customAdUrl = value
                        "ads_enabled" -> adsEnabled = value.toBooleanStrictOrNull() ?: true
                        "ad_interval_seconds" -> adIntervalSeconds = value.toIntOrNull() ?: 120
                    }
                }
                "button" -> {
                    if (parts.size >= 5) {
                        buttons.add(
                            AppButton(
                                id = parts[1],
                                name = parts[2],
                                logoUrl = parts[3],
                                targetUrl = parts[4]
                            )
                        )
                    }
                }
            }
        }
        
        return AppConfig(
            notice = notice,
            customAdUrl = customAdUrl,
            adsEnabled = adsEnabled,
            adIntervalSeconds = adIntervalSeconds,
            buttons = if (buttons.isEmpty()) AppConfig().buttons else buttons
        )
    }
}
