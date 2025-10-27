package com.example.Ctrip.home

import java.time.LocalDate

// Data classes for Date Selection
data class DateSelectData(
    val dateMode: DateMode,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val monthsData: List<MonthData>,
    val totalNights: Int
)

data class DateMode(
    val id: String,
    val title: String,
    val isSelected: Boolean,
    val isNew: Boolean = false
)

data class MonthData(
    val year: Int,
    val month: Int,
    val title: String,
    val days: List<DayData>
)

data class DayData(
    val date: LocalDate,
    val dayOfMonth: Int,
    val dayOfWeek: Int, // 1=Sunday, 2=Monday, etc.
    val isToday: Boolean = false,
    val isCheckIn: Boolean = false,
    val isCheckOut: Boolean = false,
    val isInRange: Boolean = false,
    val isSelectable: Boolean = true,
    val specialEvent: String? = null // For holidays like "万圣夜", "感恩节" etc.
)

data class DateSelection(
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val nights: Int
)

// Contract interfaces
interface HotelDateSelectTabContract {
    
    interface View {
        fun showDateData(data: DateSelectData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onDateSelectionCompleted(selection: DateSelection)
        fun updateDateSelection(checkIn: LocalDate, checkOut: LocalDate)
        fun showInvalidDateSelection(message: String)
        fun updateSelectionStatus(isSelectingCheckIn: Boolean)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadDateData()
        fun onDateModeSelected(modeId: String)
        fun onDateClicked(date: LocalDate)
        fun onCompleteClicked()
        fun calculateNights(checkIn: LocalDate, checkOut: LocalDate): Int
        fun isValidDateSelection(checkIn: LocalDate, checkOut: LocalDate): Boolean
    }
    
    interface Model {
        fun getDateSelectData(): DateSelectData
        fun updateCheckInDate(date: LocalDate)
        fun updateCheckOutDate(date: LocalDate)
        fun updateDateMode(modeId: String)
        fun generateMonthsData(startMonth: LocalDate, endMonth: LocalDate): List<MonthData>
        fun getSpecialEvents(): Map<LocalDate, String>
        fun saveDateSelection(selection: DateSelection)
        fun resetSelection()
    }
}