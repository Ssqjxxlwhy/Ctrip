package com.example.Ctrip.my

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext

@Composable
fun MyTabScreen() {
    val context = LocalContext.current
    val myTabView = remember {
        try {
            MyTabView(context).apply { initialize() }
        } catch (e: Exception) {
            null
        }
    }
    
    if (myTabView != null) {
        myTabView.MyTabScreen()
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text("个人中心加载失败")
        }
    }
}