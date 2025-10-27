package com.example.Ctrip.home

import com.example.Ctrip.model.HotelInfo
import java.time.LocalDate

data class HotelBookingData(
    val promotionBanner: HotelPromotionBanner,
    val hotelCategories: List<HotelCategory>,
    val searchParams: HotelSearchParams,
    val quickSearchTags: List<String>,
    val benefitSections: List<BenefitSection>,
    val bottomSections: List<BottomSection>
)

data class HotelPromotionBanner(
    val title: String,
    val subtitle: String,
    val backgroundColor: String
)

data class HotelCategory(
    val id: String,
    val title: String,
    val isSelected: Boolean
)

data class HotelSearchParams(
    val city: String,
    val checkInDate: LocalDate,
    val checkOutDate: LocalDate,
    val roomCount: Int,
    val adultCount: Int,
    val childCount: Int,
    val priceRange: String = "",
    val starLevel: String = ""
)

data class BenefitSection(
    val title: String,
    val discountText: String,
    val benefitText: String,
    val backgroundColor: String
)

data class BottomSection(
    val id: String,
    val title: String,
    val subtitle: String,
    val icon: String
)

interface HotelBookingTabContract {
    
    interface View {
        fun showHotelData(data: HotelBookingData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToHotelSearch(searchParams: HotelSearchParams)
        fun showDatePicker(currentDate: LocalDate)
        fun showRoomGuestPicker(currentParams: HotelSearchParams)
        fun showCitySelector(currentCity: String)
        fun showImageSearch()
        fun showVoiceSearch()
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadHotelData()
        fun onCategorySelected(categoryId: String)
        fun onCityClicked()
        fun onDateClicked()
        fun onRoomGuestClicked()
        fun onSearchClicked()
        fun onImageSearchClicked()
        fun onVoiceSearchClicked()
        fun onQuickTagClicked(tag: String)
        fun onBenefitSectionClicked(section: BenefitSection)
        fun onBottomSectionClicked(sectionId: String)
        fun updateSearchParams(params: HotelSearchParams)
    }
    
    interface Model {
        fun getHotelBookingData(): HotelBookingData?
        fun updateSearchParams(params: HotelSearchParams)
        fun saveSearchAction(searchParams: HotelSearchParams)
    }
}