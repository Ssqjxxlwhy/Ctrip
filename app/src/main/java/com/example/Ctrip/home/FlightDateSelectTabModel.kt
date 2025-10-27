package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FlightDateSelectTabModel(private val context: Context) : FlightDateSelectTabContract.Model {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("flight_date_select_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    override fun getFlightDateSelectionData(): FlightDateSelectionData? {
        return try {
            val months = generateMonthsData()
            FlightDateSelectionData(
                months = months,
                selectedDate = null,
                isDirectFlightOnly = getDirectFlightPreference(),
                weekendReturnOffers = getWeekendOffers()
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateMonthsData(): List<FlightMonthData> {
        val months = mutableListOf<FlightMonthData>()
        val currentDate = LocalDate.now()
        
        // Generate data for October, November, December 2025
        for (monthOffset in 0..2) {
            val targetDate = LocalDate.of(2025, 10 + monthOffset, 1)
            val monthData = generateMonthData(targetDate)
            months.add(monthData)
        }
        
        return months
    }
    
    private fun generateMonthData(firstDayOfMonth: LocalDate): FlightMonthData {
        val year = firstDayOfMonth.year
        val month = firstDayOfMonth.monthValue
        val monthName = "${year}年${month}月"
        
        val days = mutableListOf<FlightDayData>()
        val daysInMonth = firstDayOfMonth.lengthOfMonth()
        val flightPrices = getFlightPricesForMonth(year, month)
        val specialDates = getSpecialDates()
        val today = LocalDate.now()
        
        for (dayOfMonth in 1..daysInMonth) {
            val date = LocalDate.of(year, month, dayOfMonth)
            val dayData = FlightDayData(
                date = date,
                dayOfMonth = dayOfMonth,
                price = flightPrices[date],
                isToday = date == today,
                isWeekend = date.dayOfWeek.value >= 6,
                isSelected = false,
                isAvailable = flightPrices.containsKey(date),
                specialMarker = specialDates[date]
            )
            days.add(dayData)
        }
        
        return FlightMonthData(year, month, monthName, days)
    }
    
    override fun getFlightPricesForMonth(year: Int, month: Int): Map<LocalDate, String> {
        val prices = mutableMapOf<LocalDate, String>()
        val daysInMonth = LocalDate.of(year, month, 1).lengthOfMonth()
        
        // Generate sample flight prices
        for (dayOfMonth in 1..daysInMonth) {
            val date = LocalDate.of(year, month, dayOfMonth)
            val basePrice = when (month) {
                10 -> 800 + (dayOfMonth * 10) // October prices
                11 -> 750 + (dayOfMonth * 12) // November prices  
                12 -> 900 + (dayOfMonth * 8)  // December prices
                else -> 700
            }
            
            // Add weekend premium
            val weekendMultiplier = if (date.dayOfWeek.value >= 6) 1.2 else 1.0
            val finalPrice = (basePrice * weekendMultiplier).toInt()
            
            prices[date] = "¥$finalPrice"
        }
        
        return prices
    }
    
    override fun getSpecialDates(): Map<LocalDate, String> {
        return mapOf(
            LocalDate.of(2025, 10, 31) to "万圣夜",
            LocalDate.of(2025, 11, 27) to "感恩节",
            LocalDate.now() to "今天"
        )
    }
    
    override fun getWeekendOffers(): List<WeekendOffer> {
        return listOf(
            WeekendOffer(
                id = "weekend_oct_2025",
                monthName = "10月",
                title = "周末往返低价",
                url = "https://example.com/weekend-offers/2025/10"
            ),
            WeekendOffer(
                id = "weekend_nov_2025",
                monthName = "11月", 
                title = "周末往返低价",
                url = "https://example.com/weekend-offers/2025/11"
            ),
            WeekendOffer(
                id = "weekend_dec_2025",
                monthName = "12月",
                title = "周末往返低价", 
                url = "https://example.com/weekend-offers/2025/12"
            )
        )
    }
    
    override fun toggleDirectFlightFilter(isDirectOnly: Boolean) {
        sharedPreferences.edit()
            .putBoolean("direct_flight_only", isDirectOnly)
            .apply()
    }
    
    private fun getDirectFlightPreference(): Boolean {
        return sharedPreferences.getBoolean("direct_flight_only", false)
    }
    
    override fun saveDateSelection(date: LocalDate) {
        val selectionData = mapOf(
            "action" to "flight_date_selected",
            "timestamp" to System.currentTimeMillis(),
            "selected_date" to date.format(DateTimeFormatter.ISO_LOCAL_DATE),
            "year" to date.year,
            "month" to date.monthValue,
            "day" to date.dayOfMonth
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_flight_date_selection_${System.currentTimeMillis()}", actionJson)
            .putString("current_selected_flight_date", date.format(DateTimeFormatter.ISO_LOCAL_DATE))
            .apply()
    }
}