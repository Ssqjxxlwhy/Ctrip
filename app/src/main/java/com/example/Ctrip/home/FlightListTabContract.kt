package com.example.Ctrip.home

import java.time.LocalDate

// Data classes for Flight List
data class FlightListData(
    val routeInfo: RouteInfo,
    val dateOptions: List<DateOption>,
    val promotionBanner: FlightListPromotionBanner,
    val membershipInfo: MembershipInfo,
    val filterTags: List<FlightFilterTag>,
    val flightList: List<FlightItem>,
    val sortOptions: List<SortOption>,
    val selectedDateIndex: Int = 1 // Default to "明天"
)

data class RouteInfo(
    val departureCity: String, // "上海"
    val arrivalCity: String,   // "北京"
    val showCalendar: Boolean = true,
    val showLowPriceAlert: Boolean = true,
    val showMore: Boolean = true
)

data class DateOption(
    val id: String,
    val label: String,    // "今天", "明天", "后天", "周二", "周三"
    val date: LocalDate,
    val displayDate: String, // "25", "10-26", "27", "28", "29"
    val price: String,   // "¥500", "¥332", "¥410"
    val isSelected: Boolean = false,
    val isToday: Boolean = false
)

data class FlightListPromotionBanner(
    val title: String,        // "机票次卡198元起"
    val subtitle: String,     // "抢15元国内机票券"
    val actionText: String,   // "免费抢"
    val backgroundColor: String = "#FF4081",
    val icon: String = "✈️"
)

data class MembershipInfo(
    val level: String,        // "普通会员"
    val upgradeText: String,  // "升级解锁白银权益"
    val benefits: String,     // "酒店免费取消 延迟退房 等"
    val showArrow: Boolean = true
)

data class FlightFilterTag(
    val id: String,
    val text: String,         // "筛选了", "下午出发", "大机型", "免费托运行李", "隐藏共享"
    val isSelected: Boolean = false,
    val hasDropdown: Boolean = false
)

data class FlightItem(
    val id: String,
    val departureTime: String,     // "07:20"
    val arrivalTime: String,       // "09:40"
    val departureAirport: String,  // "浦东T2"
    val arrivalAirport: String,    // "大兴"
    val airline: String,           // "吉祥"
    val flightNumber: String,      // "HO1251"
    val aircraftType: String,      // "空客321(中)"
    val price: String,             // "¥332"
    val originalPrice: String?,    // "¥345"
    val discount: String?,         // "限青老年票"
    val tags: List<String>,        // ["随行送机最高63折", "含共享"]
    val hasWifi: Boolean = false,
    val isFavorite: Boolean = false,
    val airlineIcon: String = "✈️",
    val priceColor: String = "#2196F3",
    val cabinClass: String = "economy"  // "economy" 经济舱, "business" 公务/头等舱
)

data class SortOption(
    val id: String,
    val title: String,            // "推荐排序", "直飞优先", "时间排序", "价格排序"
    val icon: String,             // "👍", "✈️", "⏰", "💰"
    val isSelected: Boolean = false
)

// Contract interfaces
interface FlightListTabContract {
    
    interface View {
        fun showFlightListData(data: FlightListData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onFlightSelected(flightId: String)
        fun onDateChanged(dateId: String)
        fun onFilterChanged(filterId: String)
        fun onSortChanged(sortId: String)
        fun onBack()
        fun showLowPriceAlert()
        fun showMoreOptions()
        fun showCalendar()
        fun showPromotionDetails()
        fun showMembershipUpgrade()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadFlightListData(departureCity: String, arrivalCity: String, selectedDate: LocalDate, cabinClass: String = "economy")
        fun onFlightItemClicked(flightId: String)
        fun onDateOptionClicked(dateId: String)
        fun onFilterTagClicked(filterId: String)
        fun onSortOptionClicked(sortId: String)
        fun onBackClicked()
        fun onLowPriceAlertClicked()
        fun onMoreOptionsClicked()
        fun onCalendarClicked()
        fun onPromotionBannerClicked()
        fun onMembershipInfoClicked()
        fun refreshFlightList()
    }
    
    interface Model {
        fun getFlightListData(departureCity: String, arrivalCity: String, selectedDate: LocalDate, cabinClass: String = "economy"): FlightListData?
        fun getDateOptions(baseDate: LocalDate): List<DateOption>
        fun getFlightList(departureCity: String, arrivalCity: String, date: LocalDate, cabinClass: String = "economy"): List<FlightItem>
        fun getFilterTags(): List<FlightFilterTag>
        fun getSortOptions(): List<SortOption>
        fun applyFilters(flights: List<FlightItem>, activeFilters: List<String>): List<FlightItem>
        fun sortFlights(flights: List<FlightItem>, sortBy: String): List<FlightItem>
        fun saveFlightSelection(flightId: String)
        fun saveDateSelection(dateId: String)
        fun saveFilterPreferences(activeFilters: List<String>)
        fun saveSortPreference(sortId: String)
    }
}