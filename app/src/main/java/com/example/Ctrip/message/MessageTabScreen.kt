package com.example.Ctrip.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

@Composable
fun MessageTabScreen() {
    val context = LocalContext.current
    val messageTabView = MessageTabView(context).apply { initialize() }
    messageTabView.MessageTabScreen()
}