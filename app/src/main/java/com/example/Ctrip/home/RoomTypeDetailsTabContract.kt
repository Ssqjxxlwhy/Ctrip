package com.example.Ctrip.home

// Data classes for Room Type Details
data class RoomTypeDetailsData(
    val roomType: RoomType,
    val searchParams: HotelListSearchParams,
    val hotelInfo: HotelDetailInfo,
    val imageGallery: List<RoomImage>,
    val currentImageIndex: Int = 0,
    val roomFeatures: List<RoomFeature>,
    val bedInfo: BedInfo,
    val breakfastInfo: BreakfastInfo,
    val roomFacilities: List<RoomFacility>,
    val giftServices: List<GiftService>,
    val specialOffers: List<SpecialOffer>,
    val priceInfo: PriceInfo
)

data class RoomImage(
    val id: String,
    val url: String,
    val title: String = ""
)

data class RoomFeature(
    val icon: String,
    val title: String,
    val description: String
)

data class BedInfo(
    val bedType: String,
    val bedSize: String,
    val extraBedPolicy: String
)

data class BreakfastInfo(
    val included: Boolean,
    val description: String = "无早餐"
)

data class RoomFacility(
    val id: String,
    val name: String,
    val icon: String
)

data class GiftService(
    val id: String,
    val title: String,
    val description: String,
    val quantity: String = "1份"
)

data class SpecialOffer(
    val id: String,
    val title: String,
    val subtitle: String,
    val description: String,
    val discount: String
)

data class PriceInfo(
    val currentPrice: Int,
    val originalPrice: Int? = null,
    val discount: String = "",
    val priceDescription: String = "首住特惠"
)

// Contract interfaces
interface RoomTypeDetailsTabContract {
    
    interface View {
        fun showRoomTypeDetailsData(data: RoomTypeDetailsData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onImageChanged(index: Int)
        fun onFacilitiesExpanded()
        fun onGiftDetailsShown()
        fun onSpecialOfferClicked(offer: SpecialOffer)
        fun onContactHotel()
        fun onAddToCart()
        fun onBookingClicked()
        fun navigateToInfoConfirmation(roomType: RoomType, searchParams: HotelListSearchParams)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadRoomTypeDetails(roomType: RoomType, searchParams: HotelListSearchParams)
        fun onBackPressed()
        fun onChatClicked()
        fun onImageClicked(index: Int)
        fun onViewAllFacilitiesClicked()
        fun onGiftDetailsClicked()
        fun onSpecialOfferClicked(offer: SpecialOffer)
        fun onContactHotelClicked()
        fun onAddToCartClicked()
        fun onBookingClicked()
    }
    
    interface Model {
        fun getRoomTypeDetailsData(roomType: RoomType, searchParams: HotelListSearchParams): RoomTypeDetailsData?
        fun addToCart(roomTypeId: String, searchParams: HotelListSearchParams)
        fun saveRoomTypeDetailsAction(action: String, data: Map<String, Any>)
    }
}