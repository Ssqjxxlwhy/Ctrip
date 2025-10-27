package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FlightDateSelectTabView(private val context: Context) : FlightDateSelectTabContract.View {
    
    private lateinit var presenter: FlightDateSelectTabContract.Presenter
    private var flightDateData by mutableStateOf<FlightDateSelectionData?>(null)
    private var isLoading by mutableStateOf(false)
    private var isDirectFlightOnly by mutableStateOf(false)
    
    fun initialize() {
        val model = FlightDateSelectTabModel(context)
        presenter = FlightDateSelectTabPresenter(model)
        presenter.attachView(this)
        presenter.loadFlightDateData()
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun FlightDateSelectTabScreen(
        onDateSelected: (LocalDate) -> Unit = {},
        onClose: () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 顶部标题栏
                TopBarSection(onClose = onClose)
                
                // 星期栏
                WeekdayHeaderSection()
                
                // 日历区域
                flightDateData?.let { data ->
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(24.dp)
                    ) {
                        items(data.months) { monthData ->
                            MonthCalendarSection(
                                monthData = monthData,
                                weekendOffer = data.weekendReturnOffers.find { 
                                    it.monthName == "${monthData.month}月" 
                                },
                                onDateClicked = { date ->
                                    presenter.onDateClicked(date)
                                    onDateSelected(date)
                                },
                                onWeekendOfferClicked = { offerId ->
                                    presenter.onWeekendOfferClicked(offerId)
                                }
                            )
                        }
                    }
                }
                
                // 页面底部说明文字
                BottomDisclaimerSection()
            }
            
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
    private fun TopBarSection(onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 左侧关闭按钮
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    modifier = Modifier
                        .clickable { 
                            presenter.onCloseClicked()
                            onClose()
                        }
                        .size(24.dp),
                    tint = Color(0xFF333333)
                )
                
                // 中间标题
                Text(
                    text = "选择出发日期",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )
                
                // 右侧仅看直飞开关
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "仅看直飞",
                        fontSize = 14.sp,
                        color = Color(0xFF666666)
                    )
                    Switch(
                        checked = isDirectFlightOnly,
                        onCheckedChange = { checked ->
                            presenter.onDirectFlightToggled(checked)
                        },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF007AFF),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color(0xFFE0E0E0)
                        )
                    )
                }
            }
        }
    }
    
    @Composable
    private fun WeekdayHeaderSection() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")
                weekdays.forEach { day ->
                    Text(
                        text = day,
                        fontSize = 14.sp,
                        color = Color(0xFF666666),
                        textAlign = TextAlign.Center,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun MonthCalendarSection(
        monthData: FlightMonthData,
        weekendOffer: WeekendOffer?,
        onDateClicked: (LocalDate) -> Unit,
        onWeekendOfferClicked: (String) -> Unit
    ) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(8.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // 月份标题和周末优惠链接
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = monthData.monthName,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color(0xFF333333)
                    )
                    
                    weekendOffer?.let { offer ->
                        Text(
                            text = offer.title,
                            fontSize = 14.sp,
                            color = Color(0xFF007AFF),
                            modifier = Modifier.clickable {
                                onWeekendOfferClicked(offer.id)
                            }
                        )
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                // 日历网格
                CalendarGrid(
                    monthData = monthData,
                    onDateClicked = onDateClicked
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun CalendarGrid(
        monthData: FlightMonthData,
        onDateClicked: (LocalDate) -> Unit
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.height(240.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // Add empty cells for days before the first day of month
            val firstDayOfWeek = monthData.days.firstOrNull()?.date?.dayOfWeek?.value ?: 1
            val emptyCellsCount = if (firstDayOfWeek == 7) 0 else firstDayOfWeek
            
            items(emptyCellsCount) {
                Spacer(modifier = Modifier.size(40.dp))
            }
            
            items(monthData.days) { dayData ->
                DateCell(
                    dayData = dayData,
                    onDateClicked = onDateClicked
                )
            }
        }
    }
    
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun DateCell(
        dayData: FlightDayData,
        onDateClicked: (LocalDate) -> Unit
    ) {
        Card(
            modifier = Modifier
                .size(40.dp)
                .clickable(enabled = dayData.isAvailable) {
                    onDateClicked(dayData.date)
                },
            colors = CardDefaults.cardColors(
                containerColor = when {
                    dayData.isSelected -> Color(0xFF007AFF)
                    dayData.isToday -> Color(0xFFFFE4B5)
                    dayData.isWeekend -> Color(0xFFF0F8FF)
                    else -> Color.Transparent
                }
            ),
            shape = RoundedCornerShape(4.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = dayData.dayOfMonth.toString(),
                    fontSize = 12.sp,
                    fontWeight = if (dayData.isSelected || dayData.isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        dayData.isSelected -> Color.White
                        dayData.isToday -> Color(0xFF007AFF)
                        !dayData.isAvailable -> Color(0xFFCCCCCC)
                        else -> Color(0xFF333333)
                    }
                )
                
                dayData.price?.let { price ->
                    Text(
                        text = price,
                        fontSize = 8.sp,
                        color = when {
                            dayData.isSelected -> Color.White
                            else -> Color(0xFF666666)
                        }
                    )
                }
                
                dayData.specialMarker?.let { marker ->
                    Text(
                        text = marker,
                        fontSize = 6.sp,
                        color = Color(0xFFFF6B6B)
                    )
                }
            }
        }
    }
    
    @Composable
    private fun BottomDisclaimerSection() {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(0.dp)
        ) {
            Text(
                text = "所选日期为出发地日期，显示单成人价，变价频繁以实际支付价为准",
                fontSize = 12.sp,
                color = Color(0xFF999999),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }
    }
    
    // View interface implementations
    override fun showFlightDateData(data: FlightDateSelectionData) {
        flightDateData = data
        isDirectFlightOnly = data.isDirectFlightOnly
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
    
    override fun onDateSelected(date: LocalDate) {
        // Handle date selection
    }
    
    override fun onClose() {
        // Handle close action
    }
    
    override fun updateDirectFlightToggle(isDirectOnly: Boolean) {
        isDirectFlightOnly = isDirectOnly
    }
    
    override fun navigateToWeekendOffers(offerId: String) {
        Toast.makeText(context, "打开周末优惠: $offerId", Toast.LENGTH_SHORT).show()
    }
}