package com.example.Ctrip.home

import java.time.LocalDate

data class FlightBookingData(
    val promotionBanner: FlightPromotionBanner,
    val transportTabs: List<TransportTab>,
    val tripTypeTabs: List<TripTypeTab>,
    val searchParams: FlightSearchParams,
    val cabinTypes: List<CabinType>,
    val serviceFeatures: List<ServiceFeature>,
    val membershipSections: List<MembershipSection>,
    val inspirationSections: List<InspirationSection>,
    val bottomFeatures: List<BottomFeature>
)

data class FlightPromotionBanner(
    val title: String,
    val subtitle: String,
    val backgroundColor: String
)

data class TransportTab(
    val id: String,
    val title: String,
    val isSelected: Boolean,
    val hasDiscount: Boolean = false
)

data class TripTypeTab(
    val id: String,
    val title: String,
    val isSelected: Boolean
)

data class FlightSearchParams(
    val departureCity: String,
    val arrivalCity: String,
    val departureDate: LocalDate,
    val returnDate: LocalDate? = null,
    val passengerInfo: PassengerInfo,
    val selectedCabin: String
)

data class PassengerInfo(
    val adultCount: Int,
    val childCount: Int,
    val infantCount: Int
)

data class CabinType(
    val id: String,
    val title: String,
    val isSelected: Boolean
)

data class ServiceFeature(
    val id: String,
    val title: String,
    val icon: String
)

data class MembershipSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val backgroundColor: String,
    val textColor: String
)

data class InspirationSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val imageUrl: String
)

data class BottomFeature(
    val id: String,
    val title: String,
    val icon: String
)

interface FlightBookingTabContract {
    
    interface View {
        fun showFlightData(data: FlightBookingData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToFlightSearch(searchParams: FlightSearchParams)
        fun showDatePicker(isReturn: Boolean = false)
        fun showPassengerPicker(currentInfo: PassengerInfo)
        fun showCitySelector(isDeparture: Boolean = true)
        fun swapCities()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadFlightData()
        fun onTransportTabSelected(tabId: String)
        fun onTripTypeSelected(typeId: String)
        fun onDepartureCityClicked()
        fun onArrivalCityClicked()
        fun onSwapCitiesClicked()
        fun onDateClicked(isReturn: Boolean = false)
        fun onPassengerInfoClicked()
        fun onCabinTypeSelected(cabinId: String)
        fun onSearchClicked()
        fun onServiceFeatureClicked(featureId: String)
        fun onMembershipSectionClicked(sectionId: String)
        fun onInspirationSectionClicked(sectionId: String)
        fun onBottomFeatureClicked(featureId: String)
        fun updateSearchParams(params: FlightSearchParams)
    }
    
    interface Model {
        fun getFlightBookingData(): FlightBookingData?
        fun updateSearchParams(params: FlightSearchParams)
        fun saveSearchAction(searchParams: FlightSearchParams)
    }
}