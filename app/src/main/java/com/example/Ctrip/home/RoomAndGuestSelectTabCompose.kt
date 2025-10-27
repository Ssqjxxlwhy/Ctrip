package com.example.Ctrip.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun RoomAndGuestSelectTabScreen(
    onRoomAndGuestSelected: (RoomAndGuestSelection) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { RoomAndGuestSelectTabModelImpl(context) }
    val presenter = remember { RoomAndGuestSelectTabPresenter(model) }
    
    var roomAndGuestData by remember { mutableStateOf<RoomAndGuestSelectData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    
    val view = object : RoomAndGuestSelectTabContract.View {
        override fun showRoomAndGuestData(data: RoomAndGuestSelectData) {
            roomAndGuestData = data
        }
        
        override fun showLoading() {
            isLoading = true
        }
        
        override fun hideLoading() {
            isLoading = false
        }
        
        override fun showError(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        
        override fun onRoomAndGuestSelectionCompleted(selection: RoomAndGuestSelection) {
            onRoomAndGuestSelected(selection)
            onBackPressed()
        }
        
        override fun updateSelection(roomCount: Int, adultCount: Int, childCount: Int) {
            Toast.makeText(context, "房间: ${roomCount}间, 成人: ${adultCount}人, 儿童: ${childCount}人", Toast.LENGTH_SHORT).show()
        }
        
        override fun showInvalidSelection(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(view)
        presenter.loadRoomAndGuestData()
    }
    
    DisposableEffect(Unit) {
        onDispose {
            presenter.detachView()
        }
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // Header with close button and title
        HeaderSection(
            onBackPressed = onBackPressed
        )
        
        // Tip section
        TipSection()
        
        // Selection content
        Box(modifier = Modifier.weight(1f)) {
            if (roomAndGuestData != null) {
                SelectionContent(
                    roomAndGuestData = roomAndGuestData!!,
                    onRoomCountChanged = { count -> presenter.onRoomCountChanged(count) },
                    onAdultCountChanged = { count -> presenter.onAdultCountChanged(count) },
                    onChildCountChanged = { count -> presenter.onChildCountChanged(count) }
                )
            }
        }
        
        // Complete button
        CompleteButtonSection(
            onCompleteClicked = { presenter.onCompleteClicked() }
        )
        
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
private fun HeaderSection(
    onBackPressed: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Close,
            contentDescription = "关闭",
            modifier = Modifier
                .clickable { onBackPressed() }
                .size(24.dp),
            tint = Color.Gray
        )
        
        Text(
            text = "选择客房和入住人数",
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black
        )
        
        // Placeholder for symmetry
        Spacer(modifier = Modifier.size(24.dp))
    }
}

@Composable
private fun TipSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            Icons.Default.Info,
            contentDescription = "提示",
            modifier = Modifier.size(16.dp),
            tint = Color(0xFF4A90E2)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = "入住人数较多时，试试增加间数",
            fontSize = 14.sp,
            color = Color(0xFF4A90E2)
        )
    }
}

@Composable
private fun SelectionContent(
    roomAndGuestData: RoomAndGuestSelectData,
    onRoomCountChanged: (Int) -> Unit,
    onAdultCountChanged: (Int) -> Unit,
    onChildCountChanged: (Int) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        
        // 间数选择
        SelectionRow(
            title = "间数",
            subtitle = null,
            count = roomAndGuestData.roomCount,
            onCountChanged = onRoomCountChanged,
            minCount = 1,
            maxCount = 10
        )
        
        // 成人数选择
        SelectionRow(
            title = "成人数",
            subtitle = null,
            count = roomAndGuestData.adultCount,
            onCountChanged = onAdultCountChanged,
            minCount = 1,
            maxCount = 20
        )
        
        // 儿童数选择
        SelectionRow(
            title = "儿童数",
            subtitle = "0-17岁",
            count = roomAndGuestData.childCount,
            onCountChanged = onChildCountChanged,
            minCount = 0,
            maxCount = 10
        )
    }
}

@Composable
private fun SelectionRow(
    title: String,
    subtitle: String?,
    count: Int,
    onCountChanged: (Int) -> Unit,
    minCount: Int,
    maxCount: Int
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    fontSize = 14.sp,
                    color = Color.Gray
                )
            }
        }
        
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Minus button
            IconButton(
                onClick = { 
                    if (count > minCount) {
                        onCountChanged(count - 1)
                    }
                },
                enabled = count > minCount,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (count > minCount) Color(0xFFE3F2FD) else Color(0xFFF5F5F5)
                    )
            ) {
                Text(
                    text = "−",
                    fontSize = 20.sp,
                    color = if (count > minCount) Color(0xFF4A90E2) else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
            
            // Count display
            Text(
                text = count.toString(),
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = Color.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.widthIn(min = 40.dp)
            )
            
            // Plus button
            IconButton(
                onClick = { 
                    if (count < maxCount) {
                        onCountChanged(count + 1)
                    }
                },
                enabled = count < maxCount,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(20.dp))
                    .background(
                        if (count < maxCount) Color(0xFF4A90E2) else Color(0xFFF5F5F5)
                    )
            ) {
                Text(
                    text = "+",
                    fontSize = 20.sp,
                    color = if (count < maxCount) Color.White else Color.Gray,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
private fun CompleteButtonSection(
    onCompleteClicked: () -> Unit
) {
    Button(
        onClick = onCompleteClicked,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4A90E2)),
        shape = RoundedCornerShape(8.dp)
    ) {
        Text(
            text = "完成",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}