package com.example.Ctrip.home

import java.time.LocalDate

// Data classes for Flight Date Selection
data class FlightDateSelectionData(
    val months: List<FlightMonthData>,
    val selectedDate: LocalDate?,
    val isDirectFlightOnly: Boolean = false,
    val weekendReturnOffers: List<WeekendOffer>
)

data class FlightMonthData(
    val year: Int,
    val month: Int,
    val monthName: String, // "2025年10月"
    val days: List<FlightDayData>
)

data class FlightDayData(
    val date: LocalDate,
    val dayOfMonth: Int,
    val price: String?, // "¥500", null if no flights
    val isToday: Boolean = false,
    val isWeekend: Boolean = false,
    val isSelected: Boolean = false,
    val isAvailable: Boolean = true,
    val specialMarker: String? = null // "万圣夜", "感恩节" etc.
)

data class WeekendOffer(
    val id: String,
    val monthName: String, // "10月", "11月", "12月"
    val title: String = "周末往返低价",
    val url: String
)

// Contract interfaces
interface FlightDateSelectTabContract {
    
    interface View {
        fun showFlightDateData(data: FlightDateSelectionData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onDateSelected(date: LocalDate)
        fun onClose()
        fun updateDirectFlightToggle(isDirectOnly: Boolean)
        fun navigateToWeekendOffers(offerId: String)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadFlightDateData()
        fun onDateClicked(date: LocalDate)
        fun onCloseClicked()
        fun onDirectFlightToggled(isDirectOnly: Boolean)
        fun onWeekendOfferClicked(offerId: String)
        fun onMonthChanged(year: Int, month: Int)
    }
    
    interface Model {
        fun getFlightDateSelectionData(): FlightDateSelectionData?
        fun getFlightPricesForMonth(year: Int, month: Int): Map<LocalDate, String>
        fun toggleDirectFlightFilter(isDirectOnly: Boolean)
        fun saveDateSelection(date: LocalDate)
        fun getWeekendOffers(): List<WeekendOffer>
        fun getSpecialDates(): Map<LocalDate, String> // Map of special dates to their markers
    }
}