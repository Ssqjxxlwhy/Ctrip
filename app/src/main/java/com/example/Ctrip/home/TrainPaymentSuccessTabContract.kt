package com.example.Ctrip.home

interface TrainPaymentSuccessTabContract {

    data class PaymentSuccessData(
        val orderId: String,
        val paymentAmount: Int,
        val paymentMethod: String = "微信支付",
        val trainNumber: String,
        val departureStation: String,
        val arrivalStation: String,
        val departureDate: String,
        val departureTime: String,
        val passengerName: String,
        val seatType: String
    )

    interface View {
        fun showPaymentSuccess(data: PaymentSuccessData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateToOrderDetail()
        fun navigateToHome()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPaymentSuccessData(paymentData: TicketPaymentTabContract.PaymentData)
        fun onViewOrderClicked()
        fun onContinueShoppingClicked()
    }
}
