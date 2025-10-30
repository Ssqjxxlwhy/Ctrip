package com.example.Ctrip.home

import android.content.Context

interface HotelPaymentSuccessTabModel : HotelPaymentSuccessTabContract.Model

class HotelPaymentSuccessTabModelImpl(private val context: Context) : HotelPaymentSuccessTabModel {

    override fun getPaymentSuccessData(
        paymentData: PaymentData,
        confirmationData: InfoConfirmationData
    ): HotelPaymentSuccessData? {
        return try {
            val nights = calculateNights(confirmationData.checkInInfo.dateRange)
            HotelPaymentSuccessData(
                orderId = paymentData.orderId,
                hotelName = confirmationData.hotelName,
                checkInDate = extractCheckInDate(confirmationData.checkInInfo.dateRange),
                checkOutDate = extractCheckOutDate(confirmationData.checkInInfo.dateRange),
                nights = nights,
                roomType = confirmationData.roomType.name,
                paymentAmount = paymentData.amount.toString(),
                paymentMethod = getPaymentMethodName(paymentData),
                estimatedDeliveryDate = extractCheckInDate(confirmationData.checkInInfo.dateRange)
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun extractCheckInDate(dateRange: String): String {
        // dateRange format: "10月23日今天-10月24日明天 1晚"
        return dateRange.split("-").firstOrNull()?.trim() ?: dateRange
    }

    private fun extractCheckOutDate(dateRange: String): String {
        // dateRange format: "10月23日今天-10月24日明天 1晚"
        val parts = dateRange.split("-")
        if (parts.size >= 2) {
            return parts[1].split(" ").firstOrNull()?.trim() ?: dateRange
        }
        return dateRange
    }

    private fun calculateNights(dateRange: String): Int {
        // dateRange format: "10月23日今天-10月24日明天 1晚"
        val nightsPart = dateRange.substringAfterLast(" ").replace("晚", "")
        return nightsPart.toIntOrNull() ?: 1
    }

    private fun getPaymentMethodName(paymentData: PaymentData): String {
        val selectedMethod = paymentData.paymentMethods.find { it.isSelected }
        return when (selectedMethod?.type) {
            PaymentMethodType.WECHAT_PAY -> "微信支付"
            PaymentMethodType.ALIPAY -> "支付宝"
            PaymentMethodType.BANK_CARD -> "银行卡"
            PaymentMethodType.CTRIP_PAY -> "携程支付"
            else -> "其他支付方式"
        }
    }
}
