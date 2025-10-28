package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class ServiceSelectTabModel(private val context: Context) : ServiceSelectTabContract.Model {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("service_select_data", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun getServiceSelectData(orderFormData: OrderFormData): ServiceSelectData? {
        return try {
            generateServiceSelectData(orderFormData)
        } catch (e: Exception) {
            null
        }
    }

    private fun generateServiceSelectData(orderFormData: OrderFormData): ServiceSelectData {
        val flightInfo = ServiceFlightInfo(
            route = orderFormData.flightInfo.route,
            date = orderFormData.flightInfo.date,
            cabinType = "经济舱"
        )

        val promotionBanner = ServicePromotionBanner(
            title = "安检不用排队",
            subtitle = "省心又省时"
        )

        val serviceOptions = getDefaultServiceOptions()

        val paymentInfo = PaymentInfo(
            totalPrice = orderFormData.priceBreakdown.totalPrice,
            cabinType = "经济舱",
            hasDetails = true,
            receiptInfo = ReceiptInfo(
                title = "支付完成后可开具报销凭证，安心购票",
                description = "所有行程结束后，可开具电子行程单或电子发票（电子行程单需在所有行程结束后180天内申请）"
            ),
            countdownSeconds = 300
        )

        val agreements = getDefaultAgreements()

        return ServiceSelectData(
            flightInfo = flightInfo,
            totalPrice = orderFormData.priceBreakdown.totalPrice,
            promotionBanner = promotionBanner,
            serviceOptions = serviceOptions,
            paymentInfo = paymentInfo,
            agreements = agreements
        )
    }

    private fun getDefaultServiceOptions(): List<ServiceOption> {
        return listOf(
            ServiceOption(
                id = "fast_security",
                name = "快速安检",
                price = "¥1/份",
                priceNote = "机票同订价",
                description = "",
                features = listOf("有效期免费退订", "少排队", "省时省力", "尊贵体验"),
                hasInfo = true,
                isSelected = false
            ),
            ServiceOption(
                id = "seat_selection",
                name = "选座服务",
                price = "¥0/份起",
                priceNote = "",
                description = "提前锁定心仪座位  节省线下排队时间",
                features = emptyList(),
                hasInfo = true,
                isSelected = false
            ),
            ServiceOption(
                id = "flight_insurance",
                name = "航意航延组合险",
                price = "¥40/人",
                priceNote = "",
                description = "航空意外最高¥350万  延误最高赔¥300  返航、备降赔¥100",
                features = emptyList(),
                hasInfo = true,
                isSelected = false,
                additionalInfo = "携程旗下保险代理自营平台｜本模块为投保页面，由携程保险代理..."
            ),
            ServiceOption(
                id = "vip_lounge",
                name = "贵宾休息室",
                price = "¥128/份",
                priceNote = "机票同订价",
                description = "小食饮料  酒精饮料  报刊杂志",
                features = emptyList(),
                hasInfo = true,
                isSelected = false
            )
        )
    }

    private fun getDefaultAgreements(): List<Agreement> {
        return listOf(
            Agreement(
                id = "booking_notice",
                name = "购票须知",
                isChecked = true
            ),
            Agreement(
                id = "membership_agreement",
                name = "吉祥航空会员注册协议",
                isChecked = true
            ),
            Agreement(
                id = "privacy_statement",
                name = "个人信息授权声明",
                isChecked = true
            ),
            Agreement(
                id = "battery_warning",
                name = "部分充电宝禁止携带提醒",
                isChecked = true
            )
        )
    }

    override fun saveServiceSelection(serviceId: String, isSelected: Boolean) {
        val selectionData = mapOf(
            "action" to "service_selected",
            "timestamp" to System.currentTimeMillis(),
            "service_id" to serviceId,
            "is_selected" to isSelected,
            "page" to "service_select"
        )

        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("service_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }

    override fun calculateTotalPrice(data: ServiceSelectData): Int {
        var total = data.totalPrice

        // 添加已选服务的费用
        data.serviceOptions.filter { it.isSelected }.forEach { service ->
            val price = when (service.id) {
                "fast_security" -> 1
                "seat_selection" -> 0 // 起步价
                "flight_insurance" -> 40
                "vip_lounge" -> 128
                else -> 0
            }
            total += price
        }

        return total
    }

    override fun savePaymentAction(data: ServiceSelectData) {
        val paymentAction = mapOf(
            "action" to "payment_initiated",
            "timestamp" to System.currentTimeMillis(),
            "flight_info" to data.flightInfo,
            "total_price" to calculateTotalPrice(data),
            "selected_services" to data.serviceOptions.filter { it.isSelected }.map { it.id },
            "page" to "service_select"
        )

        val actionJson = gson.toJson(paymentAction)
        sharedPreferences.edit()
            .putString("payment_action_${System.currentTimeMillis()}", actionJson)
            .apply()
    }

    override fun getCountdownDuration(): Int {
        // 返回倒计时时长（秒），默认5分钟
        return sharedPreferences.getInt("countdown_duration", 300)
    }
}
