package com.example.Ctrip.home

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
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
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Composable
fun HotelDateSelectTabScreen(
    onDateSelected: (DateSelection) -> Unit = {},
    onBackPressed: () -> Unit = {}
) {
    val context = LocalContext.current
    val model = remember { HotelDateSelectTabModelImpl(context) }
    val presenter = remember { HotelDateSelectTabPresenter(model) }
    
    var dateData by remember { mutableStateOf<DateSelectData?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var isSelectingCheckIn by remember { mutableStateOf(true) }
    
    val view = object : HotelDateSelectTabContract.View {
        override fun showDateData(data: DateSelectData) {
            dateData = data
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
        
        override fun onDateSelectionCompleted(selection: DateSelection) {
            onDateSelected(selection)
            onBackPressed()
        }
        
        override fun updateDateSelection(checkIn: LocalDate, checkOut: LocalDate) {
            Toast.makeText(context, "入住: ${checkIn.format(DateTimeFormatter.ofPattern("M月d日"))}, 离店: ${checkOut.format(DateTimeFormatter.ofPattern("M月d日"))}", Toast.LENGTH_SHORT).show()
        }
        
        override fun showInvalidDateSelection(message: String) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
        
        override fun updateSelectionStatus(isSelectingCheckInDate: Boolean) {
            isSelectingCheckIn = isSelectingCheckInDate
        }
    }
    
    LaunchedEffect(Unit) {
        presenter.attachView(view)
        presenter.loadDateData()
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
        // Header with close button and date mode tabs
        HeaderSection(
            dateData = dateData,
            isSelectingCheckIn = isSelectingCheckIn,
            onDateModeSelected = { modeId -> presenter.onDateModeSelected(modeId) },
            onBackPressed = onBackPressed,
            onResetSelection = { presenter.resetSelectionState(); presenter.loadDateData() }
        )
        
        // Week day headers
        WeekDayHeadersSection()
        
        // Calendar content
        Box(modifier = Modifier.weight(1f)) {
            if (dateData != null) {
                CalendarContent(
                    dateData = dateData!!,
                    onDateClicked = { date -> presenter.onDateClicked(date) }
                )
            }
        }
        
        // Complete button
        CompleteButtonSection(
            totalNights = dateData?.totalNights ?: 0,
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
    dateData: DateSelectData?,
    isSelectingCheckIn: Boolean,
    onDateModeSelected: (String) -> Unit,
    onBackPressed: () -> Unit,
    onResetSelection: () -> Unit
) {
    Column {
        // Top row with close button and status
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
            
            // Selection status indicator
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isSelectingCheckIn) "选择入住日期" else "选择离店日期",
                    fontSize = 14.sp,
                    color = Color(0xFF4A90E2),
                    fontWeight = FontWeight.Medium
                )
                Spacer(modifier = Modifier.width(8.dp))
                TextButton(
                    onClick = onResetSelection,
                    colors = ButtonDefaults.textButtonColors(contentColor = Color(0xFF4A90E2))
                ) {
                    Text("重新选择", fontSize = 12.sp)
                }
            }
        }
        
        // Date mode tabs
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            // Specific date tab
            Column(
                modifier = Modifier
                    .clickable { onDateModeSelected("specific") }
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "指定日期",
                    fontSize = 18.sp,
                    fontWeight = if (dateData?.dateMode?.id == "specific") FontWeight.Medium else FontWeight.Normal,
                    color = if (dateData?.dateMode?.id == "specific") Color(0xFF4A90E2) else Color.Gray
                )
                if (dateData?.dateMode?.id == "specific") {
                    Spacer(modifier = Modifier.height(4.dp))
                    Box(
                        modifier = Modifier
                            .width(60.dp)
                            .height(3.dp)
                            .background(Color(0xFF4A90E2), RoundedCornerShape(2.dp))
                    )
                }
            }
            
            Spacer(modifier = Modifier.width(32.dp))
            
            // Smart low price tab
            Row(
                modifier = Modifier
                    .clickable { onDateModeSelected("smart") }
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "智能找低价",
                    fontSize = 18.sp,
                    fontWeight = if (dateData?.dateMode?.id == "smart") FontWeight.Medium else FontWeight.Normal,
                    color = if (dateData?.dateMode?.id == "smart") Color(0xFF4A90E2) else Color.Gray
                )
                Spacer(modifier = Modifier.width(4.dp))
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFF4444)),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "New",
                        modifier = Modifier.padding(4.dp, 2.dp),
                        fontSize = 10.sp,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun WeekDayHeadersSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        val weekDays = listOf("日", "一", "二", "三", "四", "五", "六")
        weekDays.forEachIndexed { index, day ->
            Text(
                text = day,
                fontSize = 14.sp,
                color = if (index == 0 || index == 6) Color(0xFFFF6666) else Color.Black,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
private fun CalendarContent(
    dateData: DateSelectData,
    onDateClicked: (LocalDate) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(dateData.monthsData) { monthData ->
            MonthSection(
                monthData = monthData,
                onDateClicked = onDateClicked
            )
        }
    }
}

@Composable
private fun MonthSection(
    monthData: MonthData,
    onDateClicked: (LocalDate) -> Unit
) {
    Column {
        // Month header
        Text(
            text = monthData.title,
            fontSize = 20.sp,
            fontWeight = FontWeight.Medium,
            color = Color.Black,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        // Calendar grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.heightIn(min = 200.dp, max = 400.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Add empty cells for the first week if needed
            val firstDayOfWeek = if (monthData.days.isNotEmpty()) {
                monthData.days.first().dayOfWeek
            } else 1
            
            repeat(firstDayOfWeek - 1) {
                item {
                    Spacer(modifier = Modifier.height(40.dp))
                }
            }
            
            items(monthData.days) { dayData ->
                DayCell(
                    dayData = dayData,
                    onDateClicked = onDateClicked
                )
            }
        }
    }
}

@Composable
private fun DayCell(
    dayData: DayData,
    onDateClicked: (LocalDate) -> Unit
) {
    val backgroundColor = when {
        dayData.isCheckIn || dayData.isCheckOut -> Color(0xFF4A90E2)
        dayData.isInRange -> Color(0xFFE3F2FD)
        else -> Color.Transparent
    }
    
    val textColor = when {
        !dayData.isSelectable -> Color.Gray
        dayData.isCheckIn || dayData.isCheckOut -> Color.White
        else -> Color.Black
    }
    
    Column(
        modifier = Modifier
            .height(70.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor)
            .clickable(enabled = dayData.isSelectable) { 
                onDateClicked(dayData.date) 
            }
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = dayData.dayOfMonth.toString(),
            fontSize = 18.sp,
            color = textColor,
            fontWeight = if (dayData.isCheckIn || dayData.isCheckOut) FontWeight.Medium else FontWeight.Normal
        )
        
        when {
            dayData.isToday && dayData.isCheckIn -> {
                Text(
                    text = "今天 入住",
                    fontSize = 9.sp,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
            dayData.isToday -> {
                Text(
                    text = "今天",
                    fontSize = 9.sp,
                    color = textColor
                )
            }
            dayData.isCheckIn -> {
                Text(
                    text = "入住",
                    fontSize = 9.sp,
                    color = textColor
                )
            }
            dayData.isCheckOut -> {
                Text(
                    text = "离店",
                    fontSize = 9.sp,
                    color = textColor
                )
            }
            dayData.specialEvent != null -> {
                Text(
                    text = dayData.specialEvent!!,
                    fontSize = 9.sp,
                    color = Color(0xFF666666),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CompleteButtonSection(
    totalNights: Int,
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
            text = "完成 (${totalNights}晚)",
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

