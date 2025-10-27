package com.example.Ctrip.trip

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun TripTabScreen() {
    val context = LocalContext.current
    val tripTabView = remember {
        try {
            TripTabView(context).apply { initialize() }
        } catch (e: Exception) {
            null
        }
    }
    
    if (tripTabView != null) {
        tripTabView.TripTabScreen()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("行程页面加载失败")
        }
    }
}