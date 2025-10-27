package com.example.Ctrip.home

import com.example.Ctrip.model.*

// Data classes for Info Confirmation
data class InfoConfirmationData(
    val hotelName: String,
    val roomType: RoomType,
    val searchParams: HotelListSearchParams,
    val checkInInfo: CheckInInfo,
    val cancelPolicy: String,
    val roomCount: Int,
    val guestName: String,
    val contactPhone: String,
    val priceBreakdown: PriceBreakdown,
    val promotions: List<Promotion>,
    val coupons: List<Coupon>,
    val gifts: List<Gift>,
    val benefits: List<Benefit>,
    val pointsInfo: PointsInfo,
    val urgencyMessage: String
)

data class CheckInInfo(
    val dateRange: String,
    val checkInTime: String,
    val checkOutTime: String,
    val nights: Int
)

data class PriceBreakdown(
    val originalPrice: Int,
    val finalPrice: Int,
    val discountAmount: Int,
    val description: String = "新人价"
)

data class Promotion(
    val id: String,
    val title: String,
    val discountAmount: Int,
    val description: String
)

data class Coupon(
    val id: String,
    val title: String,
    val discountAmount: Int,
    val description: String
)

data class Gift(
    val id: String,
    val title: String,
    val description: String
)

data class Benefit(
    val id: String,
    val title: String,
    val description: String
)

data class PointsInfo(
    val earnPoints: Int,
    val value: Int,
    val description: String
)

// Contract interfaces
interface InfoConfirmationTabContract {
    
    interface View {
        fun showInfoConfirmationData(data: InfoConfirmationData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun onRoomCountChanged(count: Int)
        fun onGuestNameChanged(name: String)
        fun onContactPhoneChanged(phone: String)
        fun onPriceDetailsClicked()
        fun onRoomTypeDetailsClicked()
        fun onPaymentClicked()
        fun onBackPressed()
        fun navigateToPayment(confirmationData: InfoConfirmationData)
    }
    
    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadInfoConfirmationData(roomType: RoomType, searchParams: HotelListSearchParams)
        fun onBackPressed()
        fun onRoomCountIncrease()
        fun onRoomCountDecrease()
        fun onGuestNameChanged(name: String)
        fun onContactPhoneChanged(phone: String)
        fun onPriceDetailsClicked()
        fun onRoomTypeDetailsClicked()
        fun onPaymentClicked()
    }
    
    interface Model {
        fun getInfoConfirmationData(roomType: RoomType, searchParams: HotelListSearchParams): InfoConfirmationData?
        fun updateRoomCount(roomTypeId: String, count: Int)
        fun updateGuestInfo(roomTypeId: String, guestName: String, contactPhone: String)
        fun saveBookingInfo(data: Map<String, Any>)
    }
}