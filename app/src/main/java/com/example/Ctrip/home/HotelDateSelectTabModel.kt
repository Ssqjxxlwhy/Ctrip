package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.format.DateTimeFormatter
import com.example.Ctrip.utils.DateUtils

interface HotelDateSelectTabModel : HotelDateSelectTabContract.Model

class HotelDateSelectTabModelImpl(private val context: Context) : HotelDateSelectTabModel {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("date_select_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private var currentCheckInDate = DateUtils.getCurrentDate()
    private var currentCheckOutDate = DateUtils.getTomorrowDate()
    private var currentDateMode = "specific" // "specific" or "smart"
    
    // Special events mapping
    private val specialEvents = mapOf(
        LocalDate.of(2025, 10, 31) to "万圣夜",
        LocalDate.of(2025, 11, 1) to "万圣节",
        LocalDate.of(2025, 11, 27) to "感恩节"
    )
    
    override fun getDateSelectData(): DateSelectData {
        val startMonth = LocalDate.of(2025, 10, 1)
        val endMonth = LocalDate.of(2025, 12, 31)
        
        return DateSelectData(
            dateMode = getCurrentDateMode(),
            checkInDate = currentCheckInDate,
            checkOutDate = currentCheckOutDate,
            monthsData = generateMonthsData(startMonth, endMonth),
            totalNights = ChronoUnit.DAYS.between(currentCheckInDate, currentCheckOutDate).toInt()
        )
    }
    
    override fun updateCheckInDate(date: LocalDate) {
        currentCheckInDate = date
        // If check-out date is before or same as check-in date, adjust it
        if (currentCheckOutDate <= currentCheckInDate) {
            currentCheckOutDate = currentCheckInDate.plusDays(1)
        }
    }
    
    override fun updateCheckOutDate(date: LocalDate) {
        currentCheckOutDate = date
    }
    
    override fun updateDateMode(modeId: String) {
        currentDateMode = modeId
    }
    
    override fun generateMonthsData(startMonth: LocalDate, endMonth: LocalDate): List<MonthData> {
        val months = mutableListOf<MonthData>()
        var current = startMonth.withDayOfMonth(1)
        
        while (current <= endMonth) {
            val monthTitle = "${current.year}年${current.monthValue}月"
            val daysInMonth = current.lengthOfMonth()
            val days = mutableListOf<DayData>()
            
            // Add empty days for the start of the month (to align with week days)
            val firstDayOfWeek = current.dayOfWeek.value % 7 + 1 // Convert to 1=Sunday format
            for (i in 1 until firstDayOfWeek) {
                // Add empty slots
            }
            
            // Add actual days of the month
            for (day in 1..daysInMonth) {
                val date = current.withDayOfMonth(day)
                val dayData = DayData(
                    date = date,
                    dayOfMonth = day,
                    dayOfWeek = date.dayOfWeek.value % 7 + 1,
                    isToday = date == DateUtils.getCurrentDate(),
                    isCheckIn = date == currentCheckInDate,
                    isCheckOut = date == currentCheckOutDate,
                    isInRange = date.isAfter(currentCheckInDate) && date.isBefore(currentCheckOutDate),
                    isSelectable = date >= DateUtils.getCurrentDate(),
                    specialEvent = specialEvents[date]
                )
                days.add(dayData)
            }
            
            months.add(MonthData(
                year = current.year,
                month = current.monthValue,
                title = monthTitle,
                days = days
            ))
            
            current = current.plusMonths(1)
        }
        
        return months
    }
    
    override fun getSpecialEvents(): Map<LocalDate, String> {
        return specialEvents
    }
    
    override fun saveDateSelection(selection: DateSelection) {
        val selectionData = mapOf(
            "action" to "date_selected",
            "timestamp" to System.currentTimeMillis(),
            "check_in_date" to selection.checkInDate.toString(),
            "check_out_date" to selection.checkOutDate.toString(),
            "nights" to selection.nights
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_date_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun resetSelection() {
        currentCheckInDate = DateUtils.getCurrentDate()
        currentCheckOutDate = DateUtils.getTomorrowDate()
    }
    
    private fun getCurrentDateMode(): DateMode {
        return when (currentDateMode) {
            "specific" -> DateMode("specific", "指定日期", true, false)
            "smart" -> DateMode("smart", "智能找低价", false, true)
            else -> DateMode("specific", "指定日期", true, false)
        }
    }
}