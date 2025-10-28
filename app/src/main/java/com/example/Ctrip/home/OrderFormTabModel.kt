package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson

class OrderFormTabModel(private val context: Context) : OrderFormTabContract.Model {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("order_form_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val cabinTypeModel = CabinTypeSelectTabModel(context)
    
    override fun getOrderFormData(ticketOptionId: String): OrderFormData? {
        return try {
            generateOrderFormData(ticketOptionId)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateOrderFormData(ticketOptionId: String): OrderFormData {
        // 从舱型选择页面获取航班信息
        val flightInfo = getFlightInfoFromTicketOption(ticketOptionId)
        val priceBreakdown = getPriceBreakdownFromTicketOption(ticketOptionId)
        
        return OrderFormData(
            flightInfo = flightInfo,
            priceBreakdown = priceBreakdown,
            restrictions = getOrderRestrictions(),
            contactInfo = getDefaultContactInfo(),
            passengers = getDefaultPassengers(),
            insuranceOptions = getInsuranceOptions()
        )
    }
    
    private fun getFlightInfoFromTicketOption(ticketOptionId: String): OrderFlightInfo {
        // 根据票价选项ID推断航班信息
        return when {
            ticketOptionId.contains("405") -> OrderFlightInfo(
                tripType = "单程",
                route = "上海→北京",
                date = "10-29 周三",
                time = "07:20",
                airline = "吉祥",
                flightNumber = "HO1251"
            )
            ticketOptionId.contains("425") -> OrderFlightInfo(
                tripType = "单程",
                route = "上海→北京", 
                date = "10-29 周三",
                time = "06:25",
                airline = "海南航空",
                flightNumber = "HU7614"
            )
            ticketOptionId.contains("445") -> OrderFlightInfo(
                tripType = "单程",
                route = "上海→北京",
                date = "10-29 周三", 
                time = "08:10",
                airline = "南航",
                flightNumber = "CZ3112"
            )
            else -> OrderFlightInfo(
                tripType = "单程",
                route = "上海→北京",
                date = "10-29 周三",
                time = "07:20",
                airline = "吉祥",
                flightNumber = "HO1251"
            )
        }
    }
    
    private fun getPriceBreakdownFromTicketOption(ticketOptionId: String): FlightPriceBreakdown {
        // 从票价选项ID提取价格信息
        // ID格式: economy_405 或 economy_405_2
        // 第二个部分(parts[1])是成人票价
        val adultPrice = try {
            val parts = ticketOptionId.split("_")
            if (parts.size >= 2) {
                // 提取第二部分作为价格(例如从"economy_405_2"中提取"405")
                parts[1].toIntOrNull() ?: 405
            } else {
                // 如果格式不正确,使用默认价格
                405
            }
        } catch (e: Exception) {
            // 异常情况下使用默认价格
            405
        }

        // 固定的服务费和税费
        val servicePrice = 48
        val taxPrice = 70
        // 计算总价 = 成人票价 + 服务费 + 税费
        val totalPrice = adultPrice + servicePrice + taxPrice

        return FlightPriceBreakdown(
            adultPrice = "成人¥$adultPrice",
            servicePrice = "全能保障服务¥$servicePrice",
            taxPrice = "机建燃油¥$taxPrice",
            totalPrice = totalPrice
        )
    }
    
    private fun getOrderRestrictions(): OrderRestrictions {
        return OrderRestrictions(
            idCardOnly = true,
            membershipRequired = "且吉祥航会员",
            verificationRequired = "且携程实名认证用户的乘客可订",
            additionalInfo = "且预订会员价，下单后将为携程账号已实名认证的乘机人注册/验证为吉祥航会员"
        )
    }
    
    private fun getDefaultContactInfo(): ContactInfo {
        return ContactInfo(
            countryCode = "+86",
            phoneNumber = "136 8899 7766",
            isVerified = true
        )
    }

    private fun getDefaultPassengers(): List<Passenger> {
        // 默认添加一个乘机人
        return listOf(
            Passenger(
                id = "passenger_default_001",
                name = "王芳",
                idCard = "110101199203154321",
                passengerType = "成人",
                isSelected = true
            )
        )
    }
    
    private fun getInsuranceOptions(): InsuranceOptions {
        return InsuranceOptions(
            flightInsurance = getFlightInsuranceGroup(),
            accidentInsurance = getAccidentInsurance(),
            travelInsurance = getTravelInsurance()
        )
    }
    
    private fun getFlightInsuranceGroup(): FlightInsuranceGroup {
        return FlightInsuranceGroup(
            title = "航意航延组合险(2025A)",
            hasTerms = true,
            options = listOf(
                InsuranceOption(
                    id = "none",
                    name = "无保障",
                    price = "",
                    benefits = emptyList(),
                    isSelected = true // 默认选择无保障
                ),
                InsuranceOption(
                    id = "standard",
                    name = "标准保障",
                    price = "¥40/人",
                    benefits = listOf(
                        "意外保障最高¥350万",
                        "延误最高赔¥300",
                        "返航、备降赔¥100"
                    )
                ),
                InsuranceOption(
                    id = "premium",
                    name = "尊享保障", 
                    price = "¥50/人",
                    benefits = listOf(
                        "意外保障最高¥500万",
                        "延误最高赔¥400",
                        "返航、备降赔¥200"
                    )
                )
            ),
            selectedOptionId = "none"
        )
    }
    
    private fun getAccidentInsurance(): AccidentInsurance {
        return AccidentInsurance(
            name = "航空意外险",
            price = "¥29/人",
            description = "航空意外保最高¥350万，含意外医疗、行李损失等保障",
            hasTerms = true,
            isSelected = false
        )
    }
    
    private fun getTravelInsurance(): TravelInsurance {
        return TravelInsurance(
            name = "国内旅行险",
            isExpanded = false,
            options = listOf(
                TravelInsuranceOption(
                    id = "basic",
                    name = "基础版",
                    price = "¥15/人",
                    description = "基础旅行保障"
                ),
                TravelInsuranceOption(
                    id = "enhanced",
                    name = "增强版", 
                    price = "¥25/人",
                    description = "增强旅行保障"
                )
            )
        )
    }
    
    override fun saveOrderFormData(data: OrderFormData) {
        val dataJson = gson.toJson(data)
        sharedPreferences.edit()
            .putString("current_order_form", dataJson)
            .apply()
    }
    
    override fun updateInsuranceSelection(insuranceType: String, optionId: String) {
        val selectionData = mapOf(
            "action" to "insurance_selected",
            "timestamp" to System.currentTimeMillis(),
            "insurance_type" to insuranceType,
            "option_id" to optionId,
            "page" to "order_form"
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("insurance_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun updateContactInfo(contactInfo: ContactInfo) {
        val contactJson = gson.toJson(contactInfo)
        sharedPreferences.edit()
            .putString("contact_info", contactJson)
            .apply()
    }
    
    override fun addPassenger(passenger: Passenger) {
        val passengerData = mapOf(
            "action" to "passenger_added",
            "timestamp" to System.currentTimeMillis(),
            "passenger_id" to passenger.id,
            "passenger_name" to passenger.name,
            "page" to "order_form"
        )
        
        val actionJson = gson.toJson(passengerData)
        sharedPreferences.edit()
            .putString("passenger_add_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun removePassenger(passengerId: String) {
        val removalData = mapOf(
            "action" to "passenger_removed",
            "timestamp" to System.currentTimeMillis(),
            "passenger_id" to passengerId,
            "page" to "order_form"
        )
        
        val actionJson = gson.toJson(removalData)
        sharedPreferences.edit()
            .putString("passenger_remove_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun calculateTotalPrice(data: OrderFormData): Int {
        var total = data.priceBreakdown.totalPrice
        
        // 添加保险费用
        val flightInsurance = data.insuranceOptions.flightInsurance
        if (flightInsurance.selectedOptionId != "none") {
            val selectedOption = flightInsurance.options.find { it.id == flightInsurance.selectedOptionId }
            selectedOption?.let { option ->
                val price = option.price.replace("¥", "").replace("/人", "").toIntOrNull() ?: 0
                total += price * data.passengers.size
            }
        }
        
        if (data.insuranceOptions.accidentInsurance.isSelected) {
            val price = data.insuranceOptions.accidentInsurance.price
                .replace("¥", "").replace("/人", "").toIntOrNull() ?: 0
            total += price * data.passengers.size
        }
        
        return total
    }
    
    override fun saveOrderAction(orderData: OrderFormData) {
        val orderAction = mapOf(
            "action" to "order_submitted",
            "timestamp" to System.currentTimeMillis(),
            "flight_info" to orderData.flightInfo,
            "total_price" to calculateTotalPrice(orderData),
            "passenger_count" to orderData.passengers.size,
            "page" to "order_form"
        )
        
        val actionJson = gson.toJson(orderAction)
        sharedPreferences.edit()
            .putString("order_submit_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
}