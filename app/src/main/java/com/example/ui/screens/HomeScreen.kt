package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.example.models.AppButton
import com.example.models.AppConfig
import com.example.ui.theme.NoticeBackground
import com.example.ui.theme.PrimaryNeonBlue
import kotlinx.coroutines.delay

@Composable
fun HomeScreen(
    config: AppConfig,
    onButtonClick: (AppButton) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Notice / Announcement Marquee
        if (config.notice.isNotBlank()) {
            NoticeBar(notice = config.notice)
        }

        // Custom Ad Slot (Video/Photo controlled from Admin)
        if (config.customAdUrl.isNotBlank()) {
            CustomAdHeader(imageUrl = config.customAdUrl)
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Grid of Buttons
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            modifier = Modifier.fillMaxSize(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(config.buttons) { button ->
                WatchButtonCard(button = button, onClick = { onButtonClick(button) })
            }
        }
    }
}

@Composable
fun NoticeBar(notice: String) {
    val scrollState = rememberScrollState()
    
    // Auto-scroll logic for Marquee
    LaunchedEffect(Unit) {
        while (true) {
            if (scrollState.maxValue > 0) {
                scrollState.animateScrollTo(
                    value = scrollState.maxValue,
                    animationSpec = tween(
                        durationMillis = notice.length * 100, // Speed
                        easing = LinearEasing
                    )
                )
                scrollState.scrollTo(0)
            } else {
                delay(1000)
            }
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NoticeBackground)
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "🔊",
            fontSize = 18.sp,
            modifier = Modifier.padding(end = 8.dp)
        )
        Text(
            text = notice,
            color = Color.White,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.horizontalScroll(scrollState, false)
        )
    }
}

@Composable
fun CustomAdHeader(imageUrl: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(16.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(Color.DarkGray)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = "Custom Ad",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        // Overlay label "Sponsored"
        Box(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(bottomStart = 8.dp))
                .padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Text("AD", color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun WatchButtonCard(button: AppButton, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(140.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (button.logoUrl.isNotBlank()) {
                AsyncImage(
                    model = button.logoUrl,
                    contentDescription = button.name,
                    modifier = Modifier.size(48.dp)
                )
            } else {
                // Fallback icon
                Icon(
                    painter = rememberAsyncImagePainter("https://cdn-icons-png.flaticon.com/512/8181/8181512.png"),
                    contentDescription = "Default Icon",
                    modifier = Modifier.size(48.dp),
                    tint = PrimaryNeonBlue
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Text(
                text = button.name,
                color = Color.White,
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}
