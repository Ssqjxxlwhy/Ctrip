package com.example.Ctrip.home

import java.time.LocalDate

// Data classes for Hotel Detail
data class HotelDetailData(
    val hotel: HotelItem,
    val searchParams: HotelListSearchParams,
    val imageGallery: List<HotelImage>,
    val currentImageTab: String = "cover", // cover, featured, location, album
    val hotelInfo: HotelDetailInfo,
    val amenities: List<HotelAmenity>,
    val reviewSummary: ReviewSummary,
    val locationInfo: LocationInfo,
    val promotions: List<PromotionItem>,
    val roomTypes: List<RoomType>,
    val filterTags: List<RoomFilterTag> = emptyList()
)

data class HotelImage(
    val id: String,
    val url: String,
    val type: String, // cover, featured, location, album
    val title: String = ""
)

data class HotelDetailInfo(
    val name: String,
    val badges: List<HotelBadge>,
    val openYear: String = ""
)

data class HotelBadge(
    val id: String,
    val title: String,
    val type: String // vip, year, award, etc.
)

data class HotelAmenity(
    val id: String,
    val icon: String,
    val title: String,
    val type: String = "amenity" // amenity, policy
)

data class ReviewSummary(
    val rating: Double,
    val reviewCount: Int,
    val summary: String = ""
)

data class LocationInfo(
    val address: String,
    val distanceInfo: String,
    val nearbyLandmarks: List<String> = emptyList()
)

data class PromotionItem(
    val id: String,
    val title: String,
    val discount: String,
    val type: String, // first_stay, seasonal, discount, coupon
    val backgroundColor: String = "#FF6B6B"
)

data class RoomType(
    val id: String,
    val name: String,
    val imageUrl: String,
    val description: String,
    val features: List<String>,
    val bedInfo: String,
    val area: String,
    val maxGuests: Int,
    val floor: String,
    val breakfast: String,
    val cancellationPolicy: String,
    val price: Int,
    val originalPrice: Int? = null,
    val badge: String? = null, // 本店销量No.1, etc.
    val roomCode: String = ""
)

data class RoomFilterTag(
    val id: String,
    val title: String,
    val isSelected: Boolean = false
)

// Contract interfaces
interface HotelDetailTabContract {
    
    interface View {
        fun showHotelDetailData(data: HotelDetailData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onRoomTypeSelected(roomType: RoomType)
        fun onImageTabChanged(tab: String)
        fun onDateChanged(checkInDate: LocalDate, checkOutDate: LocalDate)
        fun onGuestCountChanged(roomCount: Int, guestCount: Int)
        fun updateFavoriteStatus(isFavorite: Boolean)
        fun showShareOptions()
        fun showCartUpdated()
        fun showMoreOptions()
        fun showContactHotel()
        fun navigateToRoomTypeDetails(roomType: RoomType)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadHotelDetail(hotelId: String, searchParams: HotelListSearchParams)
        fun onImageTabClicked(tab: String)
        fun onFavoriteClicked()
        fun onShareClicked()
        fun onCartClicked()
        fun onMoreClicked()
        fun onDateClicked()
        fun onGuestCountClicked()
        fun onRoomFilterToggled(tagId: String)
        fun onRoomTypeClicked(roomType: RoomType)
        fun onContactHotelClicked()
        fun onPromotionClicked(promotion: PromotionItem)
        fun onLocationClicked()
    }
    
    interface Model {
        fun getHotelDetailData(hotelId: String, searchParams: HotelListSearchParams): HotelDetailData?
        fun toggleFavorite(hotelId: String): Boolean
        fun addToCart(hotelId: String, roomTypeId: String)
        fun getRoomTypes(hotelId: String): List<RoomType>
        fun filterRoomTypes(roomTypes: List<RoomType>, tags: List<RoomFilterTag>): List<RoomType>
        fun saveHotelDetailAction(action: String, data: Map<String, Any>)
    }
}