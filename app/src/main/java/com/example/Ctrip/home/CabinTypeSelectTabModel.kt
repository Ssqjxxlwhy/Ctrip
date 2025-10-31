package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.LocalDate

class CabinTypeSelectTabModel(private val context: Context) : CabinTypeSelectTabContract.Model {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("cabin_type_select_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    private val flightListModel = FlightListTabModel(context)
    
    override fun getCabinTypeData(flightId: String): CabinTypeSelectData? {
        return try {
            generateCabinTypeData(flightId)
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateCabinTypeData(flightId: String): CabinTypeSelectData {
        // 先获取航班数据
        val flightItem = findFlightItemById(flightId)
        val flightInfo = if (flightItem != null) {
            convertFlightItemToBasicInfo(flightItem, flightId)
        } else {
            getDefaultFlightInfo()
        }

        return CabinTypeSelectData(
            flightInfo = flightInfo,
            travelTip = getTravelTip(flightInfo),
            membershipBenefit = getMembershipBenefit(),
            cabinTypes = getCabinTypes(),
            ticketOptions = getTicketOptions(flightItem)  // 直接传入FlightItem而不是FlightBasicInfo
        )
    }
    
    private fun getFlightInfoFromId(flightId: String): FlightBasicInfo {
        // 从FlightListTabModel中获取真实的航班数据
        val flightItem = findFlightItemById(flightId)
        return if (flightItem != null) {
            convertFlightItemToBasicInfo(flightItem, flightId)
        } else {
            getDefaultFlightInfo()
        }
    }
    
    private fun findFlightItemById(flightId: String): FlightItem? {
        // 解析航班ID获取查询参数
        // ID格式: SHA_BJS_2024-10-29_1 (出发地_目的地_日期_序号)
        val parts = flightId.split("_")
        if (parts.size < 4) return null

        val departureCityCode = parts[0]
        val arrivalCityCode = parts[1]
        val dateStr = parts[2]
        val flightNumber = parts[3]

        // 转换城市代码为城市名
        val departureCity = when(departureCityCode) {
            "SHA" -> "上海"
            "BJS" -> "北京"
            "CAN" -> "广州"
            "GZ" -> "广州"
            "SZ" -> "深圳"
            "SZX" -> "深圳"
            "CTU" -> "成都"
            else -> "上海"
        }

        val arrivalCity = when(arrivalCityCode) {
            "SHA" -> "上海"
            "BJS" -> "北京"
            "CAN" -> "广州"
            "GZ" -> "广州"
            "SZ" -> "深圳"
            "SZX" -> "深圳"
            "CTU" -> "成都"
            else -> "北京"
        }

        // 解析日期
        val date = try {
            LocalDate.parse(dateStr)
        } catch (e: Exception) {
            LocalDate.now().plusDays(1)
        }

        // 获取所有航班列表（包括经济舱和公务舱）
        val economyFlights = flightListModel.getFlightList(departureCity, arrivalCity, date, "economy")
        val businessFlights = flightListModel.getFlightList(departureCity, arrivalCity, date, "business")
        val allFlights = economyFlights + businessFlights

        // 查找匹配的航班
        return allFlights.find { it.id == flightId }
    }
    
    private fun convertFlightItemToBasicInfo(flightItem: FlightItem, flightId: String): FlightBasicInfo {
        // 根据航班ID解析航线信息
        val parts = flightId.split("_")
        val departureCityCode = parts[0]
        val arrivalCityCode = parts[1]
        
        val departureCity = when(departureCityCode) {
            "SHA" -> "上海"
            "BJS" -> "北京"
            "CAN", "GZ" -> "广州"
            "SZ" -> "深圳"
            "CTU" -> "成都"
            else -> "上海"
        }
        
        val arrivalCity = when(arrivalCityCode) {
            "SHA" -> "上海"
            "BJS" -> "北京"
            "CAN", "GZ" -> "广州"
            "SZ" -> "深圳"
            "CTU" -> "成都"
            else -> "北京"
        }
        
        return FlightBasicInfo(
            route = "$departureCity — $arrivalCity",
            date = "10-29周三", // 可以根据实际日期计算
            departureTime = flightItem.departureTime,
            arrivalTime = flightItem.arrivalTime,
            departure = flightItem.departureAirport,
            arrival = flightItem.arrivalAirport,
            airline = flightItem.airline,
            flightNumber = flightItem.flightNumber,
            services = generateServicesFromFlightItem(flightItem),
            isFavorite = flightItem.isFavorite
        )
    }
    
    private fun generateServicesFromFlightItem(flightItem: FlightItem): List<String> {
        val services = mutableListOf<String>()
        
        if (flightItem.aircraftType.contains("大")) {
            services.add("大机型")
        } else if (flightItem.aircraftType.contains("中")) {
            services.add("中机型")
        }
        
        if (flightItem.hasWifi) {
            services.add("有餐食")
        }
        
        // 根据航空公司添加准点率信息
        when {
            flightItem.airline.contains("吉祥") -> services.add("到达准点率100%")
            flightItem.airline.contains("海南") -> services.add("到达准点率98%")
            flightItem.airline.contains("南航") -> services.add("到达准点率95%")
            else -> services.add("到达准点率92%")
        }
        
        return services
    }
    
    private fun findFlightItemByFlightNumber(flightNumber: String, route: String): FlightItem? {
        // 根据航线确定查询的城市
        val cities = route.split(" — ")
        if (cities.size != 2) return null

        val departureCity = cities[0]
        val arrivalCity = cities[1]
        val date = LocalDate.now().plusDays(1) // 默认明天的航班

        // 获取所有舱位的航班列表并查找匹配的航班号
        val economyFlights = flightListModel.getFlightList(departureCity, arrivalCity, date, "economy")
        val businessFlights = flightListModel.getFlightList(departureCity, arrivalCity, date, "business")
        val allFlights = economyFlights + businessFlights

        return allFlights.find { it.flightNumber == flightNumber }
    }
    
    
    private fun getDefaultFlightInfo(): FlightBasicInfo {
        return FlightBasicInfo(
            route = "上海 — 北京",
            date = "10-29周三",
            departureTime = "07:20",
            arrivalTime = "09:40",
            departure = "浦东T2",
            arrival = "大兴",
            airline = "吉祥",
            flightNumber = "HO1251",
            services = listOf("有餐食", "中机型", "到达准点率100%"),
            isFavorite = false
        )
    }
    
    private fun getTravelTip(flightInfo: FlightBasicInfo): TravelTip {
        val content = when {
            flightInfo.arrival.contains("大兴") -> "该航班到达机场为北京大兴机场。在大兴机场航站楼..."
            flightInfo.arrival.contains("首都") -> "该航班到达机场为北京首都机场。在首都机场航站楼..."
            flightInfo.arrival.contains("浦东") -> "该航班到达机场为上海浦东机场。在浦东机场航站楼..."
            flightInfo.arrival.contains("白云") -> "该航班到达机场为广州白云机场。在白云机场航站楼..."
            else -> "该航班到达机场为${flightInfo.arrival}。请注意航站楼信息..."
        }
        return TravelTip(
            title = "行程提示",
            content = content
        )
    }
    
    private fun getMembershipBenefit(): MembershipBenefit {
        return MembershipBenefit(
            title = "升级白金解锁",
            benefits = "酒店免费取消｜延迟退房 等"
        )
    }
    
    private fun getCabinTypes(): List<CabinTypeOption> {
        return listOf(
            CabinTypeOption(
                id = "economy",
                name = "经济舱",
                priceFrom = "¥392起",
                isSelected = true
            ),
            CabinTypeOption(
                id = "business",
                name = "公务/头等舱",
                priceFrom = "¥1176起",
                description = "享专属定制服务"
            )
        )
    }
    
    private fun getTicketOptions(flightItem: FlightItem?): List<TicketOption> {
        // 从实际的航班数据中获取价格信息
        val basePrice = if (flightItem != null) {
            // 提取价格数字
            flightItem.price.replace("¥", "").toIntOrNull() ?: 405
        } else {
            405
        }

        // 判断舱位类型，调整显示文案
        val cabinClass = flightItem?.cabinClass ?: "economy"
        val isBusinessClass = cabinClass == "business"
        val discountText = if (isBusinessClass) "公务舱特惠" else "经济舱2.3折"
        val cabinPrefix = if (isBusinessClass) "business" else "economy"
        val airlineName = flightItem?.airline ?: "航空公司"

        return listOf(
            TicketOption(
                id = "${cabinPrefix}_${basePrice}",
                price = "¥$basePrice",
                discount = discountText,
                baggageInfo = if (isBusinessClass) "托运行李额40KG" else "托运行李额20KG",
                refundInfo = "退票¥${(basePrice * 0.7).toInt()}起",
                changeInfo = "改签¥${(basePrice * 0.5).toInt()}起",
                pointsInfo = "积分最高可抵¥${basePrice - 5}",
                benefits = if (isBusinessClass) listOf("贵宾休息室", "优先登机", "豪华餐食") else listOf("赠接送机最高8折券"),
                restrictions = "限使用身份证，且${airlineName}会员，且携程实名认证用户的乘客可订",
                insurancePrice = "+¥48全能保障",
                buttonText = "订",
                buttonType = TicketButtonType.BOOK
            ),
            TicketOption(
                id = "${cabinPrefix}_${basePrice}_2",
                price = "¥$basePrice",
                discount = discountText,
                baggageInfo = if (isBusinessClass) "托运行李额40KG" else "托运行李额20KG",
                refundInfo = "退票¥${(basePrice * 0.7).toInt()}起",
                changeInfo = "改签¥${(basePrice * 0.5).toInt()}起",
                pointsInfo = "积分最高可抵¥${basePrice - 5}",
                benefits = if (isBusinessClass) listOf("贵宾休息室", "优先登机") else listOf("赠接送机最高8折券"),
                restrictions = "限使用身份证，且${airlineName}会员，且携程实名认证用户的乘客可订",
                buttonText = "选购",
                buttonType = TicketButtonType.SELECT
            ),
            TicketOption(
                id = "${cabinPrefix}_${basePrice + 5}",
                price = "¥${basePrice + 5}",
                discount = discountText,
                baggageInfo = if (isBusinessClass) "托运行李额40KG" else "托运行李额20KG",
                refundInfo = "退票¥${(basePrice * 0.71).toInt()}起",
                changeInfo = "改签¥${(basePrice * 0.51).toInt()}起",
                benefits = if (isBusinessClass) listOf("贵宾休息室", "优先登机", "豪华餐食", "平躺座椅") else listOf("额外11倍携程积分", "赠¥50接送机券"),
                buttonText = "选购",
                buttonType = TicketButtonType.SELECT
            ),
            TicketOption(
                id = "${cabinPrefix}_${basePrice - 13}",
                price = "¥${basePrice - 13}",
                discount = if (isBusinessClass) "公务舱早鸟价" else "经济舱2.2折",
                baggageInfo = if (isBusinessClass) "托运行李额40KG" else "托运行李额20KG",
                refundInfo = "退票¥${(basePrice * 0.68).toInt()}起",
                changeInfo = "改签¥${(basePrice * 0.48).toInt()}起",
                specialTag = if (isBusinessClass) "公务舱特惠" else "人群特惠 青老年享",
                buttonText = "选购",
                buttonType = TicketButtonType.SELECT
            )
        )
    }
    
    override fun saveCabinTypeSelection(cabinTypeId: String) {
        val selectionData = mapOf(
            "action" to "cabin_type_selected",
            "timestamp" to System.currentTimeMillis(),
            "cabin_type_id" to cabinTypeId,
            "page" to "cabin_type_select"
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_cabin_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun saveTicketOptionSelection(ticketOptionId: String) {
        val selectionData = mapOf(
            "action" to "ticket_option_selected",
            "timestamp" to System.currentTimeMillis(),
            "ticket_option_id" to ticketOptionId,
            "page" to "cabin_type_select"
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_ticket_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun saveBookingAction(ticketOptionId: String) {
        val bookingData = mapOf(
            "action" to "ticket_booked",
            "timestamp" to System.currentTimeMillis(),
            "ticket_option_id" to ticketOptionId,
            "page" to "cabin_type_select"
        )
        
        val actionJson = gson.toJson(bookingData)
        sharedPreferences.edit()
            .putString("booking_action_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun toggleFlightFavorite(flightId: String): Boolean {
        val currentState = sharedPreferences.getBoolean("flight_favorite_$flightId", false)
        val newState = !currentState
        
        sharedPreferences.edit()
            .putBoolean("flight_favorite_$flightId", newState)
            .apply()
            
        val favoriteData = mapOf(
            "action" to "flight_favorite_toggled",
            "timestamp" to System.currentTimeMillis(),
            "flight_id" to flightId,
            "is_favorite" to newState,
            "page" to "cabin_type_select"
        )
        
        val actionJson = gson.toJson(favoriteData)
        sharedPreferences.edit()
            .putString("favorite_action_${System.currentTimeMillis()}", actionJson)
            .apply()
            
        return newState
    }
}