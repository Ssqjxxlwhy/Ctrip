package com.example.Ctrip.home

import java.time.LocalDate

// Data classes for Hotel List
data class HotelListData(
    val searchParams: HotelListSearchParams,
    val sortOptions: List<HotelSortOption>,
    val filterTags: List<FilterTag>,
    val firstStayBenefit: FirstStayBenefit,
    val hotels: List<HotelItem>
)

data class HotelListSearchParams(
    val city: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val roomCount: Int,
    val guestCount: Int,
    val adultCount: Int,
    val childCount: Int,
    val searchQuery: String = ""
)

data class HotelSortOption(
    val id: String,
    val title: String,
    val isSelected: Boolean,
    val hasDropdown: Boolean = true
)

data class FilterTag(
    val id: String,
    val title: String,
    val isSelected: Boolean = false
)

data class FirstStayBenefit(
    val title: String,
    val description: String,
    val discount: String,
    val isVisible: Boolean = true
)

data class HotelItem(
    val id: String,
    val name: String,
    val imageUrl: String,
    val rating: Double,
    val reviewCount: Int,
    val favoriteCount: Int,
    val location: String,
    val description: String,
    val amenities: List<String>,
    val price: Int,
    val originalPrice: Int? = null,
    val discountInfo: String? = null,
    val benefitInfo: String? = null,
    val promotionCount: Int = 0,
    val city: String,
    val availableDates: List<LocalDate>
)

// Contract interfaces
interface HotelListTabContract {
    
    interface View {
        fun showHotelListData(data: HotelListData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun showNoResults(message: String)
        fun updateSearchResults(hotels: List<HotelItem>)
        fun onHotelSelected(hotel: HotelItem)
        fun showMapView()
        fun showMoreOptions()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadHotelList(searchParams: HotelListSearchParams)
        fun onSortOptionSelected(sortId: String)
        fun onFilterTagToggled(tagId: String)
        fun onSearchQueryChanged(query: String)
        fun onHotelClicked(hotel: HotelItem)
        fun onMapButtonClicked()
        fun onMoreButtonClicked()
        fun onFirstStayBenefitClicked()
        fun applyFiltersAndSort()
    }
    
    interface Model {
        fun getHotelListData(searchParams: HotelListSearchParams): HotelListData?
        fun searchHotels(searchParams: HotelListSearchParams): List<HotelItem>
        fun sortHotels(hotels: List<HotelItem>, sortId: String): List<HotelItem>
        fun filterHotels(hotels: List<HotelItem>, tags: List<FilterTag>): List<HotelItem>
        fun saveHotelSelection(hotel: HotelItem)
    }
}