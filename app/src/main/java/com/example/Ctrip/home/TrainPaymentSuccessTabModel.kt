package com.example.Ctrip.home

import android.content.Context

class TrainPaymentSuccessTabModel(private val context: Context) {

    fun getPaymentSuccessData(paymentData: TicketPaymentTabContract.PaymentData): TrainPaymentSuccessTabContract.PaymentSuccessData {
        return TrainPaymentSuccessTabContract.PaymentSuccessData(
            orderId = paymentData.orderId,
            paymentAmount = paymentData.finalPrice,
            paymentMethod = "微信支付",
            trainNumber = paymentData.trainNumber,
            departureStation = paymentData.departureStation,
            arrivalStation = paymentData.arrivalStation,
            departureDate = paymentData.departureDate,
            departureTime = paymentData.departureTime,
            passengerName = paymentData.passengerName,
            seatType = paymentData.seatType
        )
    }
}
