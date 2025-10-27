package com.example.Ctrip.home

import java.time.LocalDate

// Data classes for Train Ticket Booking
data class TrainTicketBookingData(
    val promotionBanner: TrainPromotionBanner,
    val transportTabs: List<TransportTabTrain>,
    val regionTabs: List<RegionTab>,
    val tripTypeTabs: List<TripTypeTabTrain>,
    val searchParams: TrainSearchParams,
    val newCustomerGift: NewCustomerGift,
    val travelToolbox: TravelToolbox,
    val lowPriceDeals: List<TrainDeal>
)

data class TrainPromotionBanner(
    val title: String,
    val subtitle: String,
    val backgroundColor: String
)

data class TransportTabTrain(
    val id: String,
    val title: String,
    val isSelected: Boolean,
    val icon: String
)

data class RegionTab(
    val id: String,
    val title: String,
    val isSelected: Boolean
)

data class TripTypeTabTrain(
    val id: String,
    val title: String,
    val isSelected: Boolean
)

data class TrainSearchParams(
    val departureCity: String,
    val arrivalCity: String,
    val departureDate: LocalDate,
    val isMultiDayCommute: Boolean,
    val isStudentTicket: Boolean,
    val isHighSpeedTrain: Boolean
)

data class NewCustomerGift(
    val title: String,
    val value: String,
    val coupons: List<CouponInfo>,
    val backgroundColor: String
)

data class CouponInfo(
    val type: String,
    val amount: String,
    val description: String
)

data class TravelToolbox(
    val title: String,
    val mainTools: List<ToolboxItem>,
    val additionalTools: List<ToolboxItem>
)

data class ToolboxItem(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String,
    val backgroundColor: String
)

data class TrainDeal(
    val id: String,
    val title: String,
    val subtitle: String,
    val backgroundColor: String
)

data class BottomNavigationItem(
    val id: String,
    val title: String,
    val icon: String,
    val isSelected: Boolean
)

// Contract interfaces
interface TrainTicketBookingTabContract {
    
    interface View {
        fun showTrainData(data: TrainTicketBookingData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToTrainSearch(searchParams: TrainSearchParams)
        fun showDatePicker()
        fun showCitySelector(isDeparture: Boolean)
        fun swapCities()
        fun showNewCustomerGift()
        fun showTravelTools()
        fun showTrainDeals(dealId: String)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadTrainData()
        fun onTransportTabSelected(tabId: String)
        fun onRegionTabSelected(regionId: String)
        fun onTripTypeSelected(typeId: String)
        fun onDepartureCityClicked()
        fun onArrivalCityClicked()
        fun onSwapCitiesClicked()
        fun onDateClicked()
        fun onMultiDayCommuteToggled(enabled: Boolean)
        fun onStudentTicketToggled(enabled: Boolean)
        fun onHighSpeedTrainToggled(enabled: Boolean)
        fun onSearchClicked()
        fun onHistoryCleared()
        fun onNewCustomerGiftClicked()
        fun onTravelToolClicked(toolId: String)
        fun onTrainDealClicked(dealId: String)
        fun onBottomNavigationClicked(itemId: String)
        fun updateSearchParams(params: TrainSearchParams)
    }
    
    interface Model {
        fun getTrainBookingData(): TrainTicketBookingData?
        fun updateSearchParams(params: TrainSearchParams)
        fun saveSearchAction(params: TrainSearchParams)
        fun clearSearchHistory()
    }
}