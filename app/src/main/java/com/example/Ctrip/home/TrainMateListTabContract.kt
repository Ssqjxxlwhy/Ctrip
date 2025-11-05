package com.example.Ctrip.home

import java.time.LocalDate

data class TrainTicket(
    val trainId: String,
    val trainNumber: String,
    val trainType: String,
    val departureCity: String,
    val arrivalCity: String,
    val departureStation: String,
    val arrivalStation: String,
    val departureDate: String,
    val departureTime: String,
    val arrivalTime: String,
    val duration: String,
    val availableSeats: Map<String, Int>,
    val prices: Map<String, Double>,
    val hasDiscount: Boolean,
    val discountAmount: Double,
    val features: List<String>
)

// 座位类型数据
data class SeatTypeInfo(
    val seatType: String,
    val available: Int,
    val price: Double
)

// 筛选条件
data class TrainFilterOptions(
    val departureStation: String?,
    val arrivalStation: String?,
    val onlyWithTickets: Boolean,
    val onlyDirect: Boolean,
    val usePoints: Boolean
)

// 排序类型
enum class SortType {
    DEFAULT,
    EARLIEST_DEPARTURE,
    SHORTEST_DURATION,
    LOWEST_PRICE
}

// 火车票日期选项
data class TrainDateOption(
    val date: LocalDate,
    val displayText: String,
    val subText: String,
    val isSelected: Boolean
)

// 出行方式
data class TransportOption(
    val id: String,
    val title: String,
    val priceFrom: String,
    val isSelected: Boolean
)

interface TrainMateListTabContract {

    interface View {
        fun showTrainList(trains: List<TrainTicket>)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun updateDateOptions(options: List<TrainDateOption>)
        fun updateTransportOptions(options: List<TransportOption>)
        fun updateFilterInfo(filterOptions: TrainFilterOptions)
        fun navigateToTrainDetail(trainId: String)
        fun showFilterDialog()
        fun navigateBack()
        fun showShareDialog()
        fun showGrabTicketInfo()
        fun showNewCustomerGift()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadTrainList(departureCity: String, arrivalCity: String, departureDate: LocalDate)
        fun onDateSelected(date: LocalDate)
        fun onTransportSelected(transportId: String)
        fun onFilterChanged(filterOptions: TrainFilterOptions)
        fun onSortChanged(sortType: SortType)
        fun onTrainClicked(trainId: String)
        fun onBackClicked()
        fun onSwapCities()
        fun onShowFilter()
        fun onShareClicked()
        fun onGrabTicketClicked()
        fun onNewCustomerGiftClicked()
    }

    interface Model {
        fun getTrainList(departureCity: String, arrivalCity: String, departureDate: LocalDate): List<TrainTicket>
        fun filterTrains(trains: List<TrainTicket>, filterOptions: TrainFilterOptions): List<TrainTicket>
        fun sortTrains(trains: List<TrainTicket>, sortType: SortType): List<TrainTicket>
        fun getDateOptions(selectedDate: LocalDate): List<TrainDateOption>
        fun getTransportOptions(): List<TransportOption>
        fun saveTrainSelection(trainId: String)
    }
}
