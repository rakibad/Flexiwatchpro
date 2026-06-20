package com.example.models

data class AppConfig(
    val notice: String = "আমাদের অ্যাপে স্বাগতম! খুব সহজেই വീഡിയോ দেখুন।",
    val customAdUrl: String = "",
    val adsEnabled: Boolean = true,
    val adIntervalSeconds: Int = 120,
    val buttons: List<AppButton> = listOf(
        AppButton("1", "Watch 1", "https://upload.wikimedia.org/wikipedia/commons/e/e1/Logo_of_YouTube_%282015-2017%29.svg", "https://youtube.com", "primary"),
        AppButton("2", "Watch 2", "https://upload.wikimedia.org/wikipedia/commons/e/e1/Logo_of_YouTube_%282015-2017%29.svg", "https://youtube.com", "primary"),
        AppButton("3", "Watch 3", "https://upload.wikimedia.org/wikipedia/commons/e/e1/Logo_of_YouTube_%282015-2017%29.svg", "https://youtube.com", "primary")
    )
)

data class AppButton(
    val id: String,
    val name: String,
    val logoUrl: String,
    val targetUrl: String,
    val type: String = "primary" // Add variation if needed
)
