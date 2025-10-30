package com.example.Ctrip.home

import android.content.Context

class TicketPaymentTabModel(private val context: Context) {

    fun getPaymentData(confirmData: InfoConfirmData): TicketPaymentTabContract.PaymentData? {
        // 根据InfoConfirmData生成PaymentData
        val orderId = "ORDER${System.currentTimeMillis()}"

        // 计算总价
        val totalPrice = confirmData.ticketPrice + confirmData.insurancePrice
        val finalPrice = totalPrice - confirmData.newCustomerDiscount

        return TicketPaymentTabContract.PaymentData(
            orderId = orderId,
            trainNumber = confirmData.trainNumber,
            departureStation = confirmData.departureStation,
            arrivalStation = confirmData.arrivalStation,
            departureTime = confirmData.departureTime,
            arrivalTime = confirmData.arrivalTime,
            departureDate = formatDepartureDate(confirmData.departureDate),
            duration = confirmData.duration,
            passengerName = confirmData.currentUser.name,
            seatType = confirmData.seatType,
            ticketPrice = confirmData.ticketPrice,
            insurancePrice = confirmData.insurancePrice,
            totalPrice = totalPrice,
            discountAmount = confirmData.newCustomerDiscount,
            finalPrice = finalPrice
        )
    }

    private fun formatDepartureDate(date: String): String {
        // 将"10-30"格式转换为"10月30日 明天"格式
        val parts = date.split("-")
        if (parts.size == 2) {
            val month = parts[0].toIntOrNull() ?: 10
            val day = parts[1].toIntOrNull() ?: 30
            return "${month}月${day}日 明天"
        }
        return "10月30日 明天"
    }

}