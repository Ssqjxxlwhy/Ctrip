package com.example.Ctrip.home

import com.example.Ctrip.model.TrainOrder

interface TicketPaymentTabContract {

    data class PaymentData(
        val orderId: String,
        val trainNumber: String,
        val departureStation: String,
        val arrivalStation: String,
        val departureTime: String,
        val arrivalTime: String,
        val departureDate: String,
        val duration: String,
        val passengerName: String,
        val seatType: String,
        val ticketPrice: Int,
        val insurancePrice: Int,
        val totalPrice: Int,
        val discountAmount: Int,
        val finalPrice: Int
    )

    interface View {
        fun showPaymentData(data: PaymentData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateBack()
        fun navigateToPaymentSuccess()
        fun showBookingNotes()
        fun showPriceDetails()
        fun cancelOrder()
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadPaymentData(confirmData: InfoConfirmData)
        fun onPayClicked()
        fun onBackClicked()
        fun onBookingNotesClicked()
        fun onPriceDetailsClicked()
        fun onCancelOrderClicked()
    }
}