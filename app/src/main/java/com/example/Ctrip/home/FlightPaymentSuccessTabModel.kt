package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.text.SimpleDateFormat
import java.util.*

class FlightPaymentSuccessTabModel(private val context: Context) : FlightPaymentSuccessTabContract.Model {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("payment_success_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun getPaymentSuccessData(serviceData: ServiceSelectData): FlightPaymentSuccessData? {
        return try {
            generatePaymentSuccessData(serviceData)
        } catch (e: Exception) {
            null
        }
    }

    private fun generatePaymentSuccessData(serviceData: ServiceSelectData): FlightPaymentSuccessData {
        // 计算最终支付金额（包含选择的服务）
        val totalAmount = calculateTotalAmount(serviceData)

        val paymentInfo = FlightPaymentSuccessInfo(
            amount = totalAmount,
            paymentMethod = "微信支付"
        )

        // 生成订单号
        val orderNumber = generateOrderNumber()

        val orderInfo = OrderSuccessInfo(
            orderNumber = orderNumber,
            status = "订单已生成",
            statusDescription = "您的订单正在处理中，我们会尽快为您安排出票",
            flightInfo = "${serviceData.flightInfo.route} ${serviceData.flightInfo.date}",
            estimatedDate = serviceData.flightInfo.date
        )

        return FlightPaymentSuccessData(
            paymentInfo = paymentInfo,
            orderInfo = orderInfo
        )
    }

    private fun calculateTotalAmount(serviceData: ServiceSelectData): Int {
        var total = serviceData.totalPrice

        // 添加已选服务的费用
        serviceData.serviceOptions.filter { it.isSelected }.forEach { service ->
            val price = when (service.id) {
                "fast_security" -> 1
                "seat_selection" -> 0
                "flight_insurance" -> 40
                "vip_lounge" -> 128
                else -> 0
            }
            total += price
        }

        return total
    }

    private fun generateOrderNumber(): String {
        val timestamp = System.currentTimeMillis()
        val random = (100000..999999).random()
        return "CT${timestamp}${random}"
    }

    override fun savePaymentSuccessAction(data: FlightPaymentSuccessData) {
        val paymentSuccessAction = mapOf(
            "action" to "payment_completed",
            "timestamp" to System.currentTimeMillis(),
            "order_number" to data.orderInfo.orderNumber,
            "amount" to data.paymentInfo.amount,
            "payment_method" to data.paymentInfo.paymentMethod,
            "flight_info" to data.orderInfo.flightInfo,
            "page" to "payment_success"
        )

        val actionJson = gson.toJson(paymentSuccessAction)
        sharedPreferences.edit()
            .putString("payment_success_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
}
