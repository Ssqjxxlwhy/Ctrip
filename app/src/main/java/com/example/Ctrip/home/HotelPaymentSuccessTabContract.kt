package com.example.Ctrip.home

// Data classes for Hotel Payment Success
data class HotelPaymentSuccessData(
    val orderId: String,
    val hotelName: String,
    val checkInDate: String,
    val checkOutDate: String,
    val nights: Int,
    val roomType: String,
    val paymentAmount: String,
    val paymentMethod: String,
    val estimatedDeliveryDate: String
)

// Contract interfaces
interface HotelPaymentSuccessTabContract {

    interface View {
        fun showPaymentSuccess(data: HotelPaymentSuccessData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToOrderDetail()
        fun navigateToHome()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPaymentSuccessData(paymentData: PaymentData, confirmationData: InfoConfirmationData)
        fun onViewOrderClicked()
        fun onContinueShoppingClicked()
    }

    interface Model {
        fun getPaymentSuccessData(paymentData: PaymentData, confirmationData: InfoConfirmationData): HotelPaymentSuccessData?
    }
}
