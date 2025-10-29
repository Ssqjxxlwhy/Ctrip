package com.example.Ctrip.home

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.*

class TrainDateSelectTabView(private val context: Context) : TrainDateSelectTabContract.View {

    private lateinit var presenter: TrainDateSelectTabContract.Presenter
    private var selectedDate by mutableStateOf<LocalDate?>(null)
    private var isLoading by mutableStateOf(false)

    fun initialize() {
        val model = TrainDateSelectTabModel(context)
        presenter = TrainDateSelectTabPresenter(model)
        presenter.attachView(this)
        selectedDate = model.getTodayDate().plusDays(10) // 默认选择10月30日
    }

    @Composable
    fun TrainDateSelectTabScreen(
        onDateSelected: (LocalDate) -> Unit = {},
        onClose: () -> Unit = {}
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // 顶部标题栏
                TopBarSection(onClose = {
                    presenter.onCloseClicked()
                    onClose()
                })

                // 提示信息
                NoticeSection()

                // 星期栏
                WeekdayHeaderSection()

                // 日历内容
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // 2025年10月
                    item {
                        MonthSection(
                            year = 2025,
                            month = 10,
                            selectedDate = selectedDate,
                            onDateClicked = { date ->
                                selectedDate = date
                                presenter.onDateSelected(date)
                                onDateSelected(date)
                                onClose()
                            }
                        )
                    }

                    // 2025年11月
                    item {
                        MonthSection(
                            year = 2025,
                            month = 11,
                            selectedDate = selectedDate,
                            onDateClicked = { date ->
                                selectedDate = date
                                presenter.onDateSelected(date)
                                onDateSelected(date)
                                onClose()
                            }
                        )
                    }

                    // 2025年12月
                    item {
                        MonthSection(
                            year = 2025,
                            month = 12,
                            selectedDate = selectedDate,
                            onDateClicked = { date ->
                                selectedDate = date
                                presenter.onDateSelected(date)
                                onDateSelected(date)
                                onClose()
                            }
                        )
                    }
                }
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

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    private fun TopBarSection(onClose: () -> Unit) {
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // 关闭按钮
                Icon(
                    Icons.Default.Close,
                    contentDescription = "关闭",
                    modifier = Modifier
                        .clickable { onClose() }
                        .size(24.dp),
                    tint = Color(0xFF333333)
                )

                // 标题
                Text(
                    text = "选择日期",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF333333)
                )

                // 占位，保持标题居中
                Spacer(modifier = Modifier.size(24.dp))
            }
        }
    }

    @Composable
    private fun NoticeSection() {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color(0xFFFFF8DC))
                .padding(vertical = 12.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "所选择的时间为当地时间",
                fontSize = 14.sp,
                color = Color(0xFFFF8800)
            )
        }
    }

    @Composable
    private fun WeekdayHeaderSection() {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            val weekdays = listOf("日", "一", "二", "三", "四", "五", "六")
            weekdays.forEach { day ->
                Text(
                    text = day,
                    fontSize = 14.sp,
                    color = if (day == "日" || day == "六") Color(0xFF999999) else Color(0xFF333333),
                    textAlign = TextAlign.Center,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }

    @Composable
    private fun MonthSection(
        year: Int,
        month: Int,
        selectedDate: LocalDate?,
        onDateClicked: (LocalDate) -> Unit
    ) {
        Column {
            // 月份标题
            Text(
                text = "${year}年${month}月",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFF333333),
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // 日历网格
            CalendarGrid(
                year = year,
                month = month,
                selectedDate = selectedDate,
                onDateClicked = onDateClicked
            )
        }
    }

    @Composable
    private fun CalendarGrid(
        year: Int,
        month: Int,
        selectedDate: LocalDate?,
        onDateClicked: (LocalDate) -> Unit
    ) {
        val firstDayOfMonth = LocalDate.of(year, month, 1)
        val daysInMonth = firstDayOfMonth.lengthOfMonth()
        val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday

        val totalCells = ((firstDayOfWeek + daysInMonth + 6) / 7) * 7

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            for (row in 0 until (totalCells / 7)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    for (col in 0..6) {
                        val cellIndex = row * 7 + col
                        val dayOfMonth = cellIndex - firstDayOfWeek + 1

                        if (dayOfMonth in 1..daysInMonth) {
                            val date = LocalDate.of(year, month, dayOfMonth)
                            DateCell(
                                date = date,
                                isSelected = date == selectedDate,
                                onDateClicked = onDateClicked
                            )
                        } else {
                            // 空白单元格
                            Box(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
        }
    }

    @Composable
    private fun RowScope.DateCell(
        date: LocalDate,
        isSelected: Boolean,
        onDateClicked: (LocalDate) -> Unit
    ) {
        val today = LocalDate.of(2025, 10, 20) // 固定的"今天"日期
        val isToday = date == today
        val isPastDate = date.isBefore(today)
        val lunarDay = getLunarDay(date)
        val specialLabel = getSpecialLabel(date)
        val hasReservation = hasReservationLabel(date)

        Box(
            modifier = Modifier
                .weight(1f)
                .aspectRatio(1f)
                .padding(2.dp)
                .background(
                    color = when {
                        isSelected -> Color(0xFF4A90E2)
                        else -> Color.Transparent
                    },
                    shape = RoundedCornerShape(8.dp)
                )
                .clickable(enabled = !isPastDate) {
                    onDateClicked(date)
                },
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // 特殊标签（今天、万圣夜等）
                if (isToday) {
                    Text(
                        text = "今天",
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else Color(0xFF333333)
                    )
                } else if (specialLabel != null) {
                    Text(
                        text = specialLabel,
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else Color(0xFF333333)
                    )
                }

                // 日期
                Text(
                    text = date.dayOfMonth.toString(),
                    fontSize = 18.sp,
                    fontWeight = if (isSelected || isToday) FontWeight.Bold else FontWeight.Normal,
                    color = when {
                        isPastDate -> Color(0xFFCCCCCC)
                        isSelected -> Color.White
                        else -> Color(0xFF333333)
                    }
                )

                // 农历或预约标签
                if (hasReservation) {
                    Text(
                        text = "预约",
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else Color(0xFF999999)
                    )
                } else {
                    Text(
                        text = lunarDay,
                        fontSize = 10.sp,
                        color = if (isSelected) Color.White else Color(0xFF999999)
                    )
                }
            }
        }
    }

    private fun getLunarDay(date: LocalDate): String {
        // 简化版农历显示，实际应该使用农历库
        val lunarDays = arrayOf(
            "初一", "初二", "初三", "初四", "初五", "初六", "初七", "初八", "初九", "初十",
            "十一", "十二", "十三", "十四", "十五", "十六", "十七", "十八", "十九", "二十",
            "廿一", "廿二", "廿三", "廿四", "廿五", "廿六", "廿七", "廿八", "廿九", "三十"
        )

        // 假设10月1日对应农历某日，这里简单计算
        val baseDate = LocalDate.of(2025, 10, 1)
        val baseLunarDay = 0 // 假设10月1日是初一
        val daysDiff = date.toEpochDay() - baseDate.toEpochDay()
        val lunarDayIndex = ((baseLunarDay + daysDiff.toInt()) % 30).let {
            if (it < 0) it + 30 else it
        }

        return lunarDays[lunarDayIndex]
    }

    private fun getSpecialLabel(date: LocalDate): String? {
        return when {
            date.monthValue == 10 && date.dayOfMonth == 31 -> "万圣夜"
            date.monthValue == 11 && date.dayOfMonth == 1 -> "万圣节"
            date.monthValue == 11 && date.dayOfMonth == 27 -> "感恩节"
            else -> null
        }
    }

    private fun hasReservationLabel(date: LocalDate): Boolean {
        // 11月和12月的大部分日期都有"预约"标签
        return when {
            date.monthValue == 11 && date.dayOfMonth in 13..30 -> true
            date.monthValue == 12 && date.dayOfMonth <= 13 -> true
            else -> false
        }
    }

    // View interface implementations
    override fun showDateSelection(selectedDate: LocalDate) {
        this.selectedDate = selectedDate
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

    override fun onDateConfirmed(date: LocalDate) {
        // Handle date confirmation
    }

    override fun onClose() {
        // Handle close action
    }
}
