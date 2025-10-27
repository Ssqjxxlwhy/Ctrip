package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class FlightListTabModel(private val context: Context) : FlightListTabContract.Model {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("flight_list_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    override fun getFlightListData(departureCity: String, arrivalCity: String, selectedDate: LocalDate): FlightListData? {
        return try {
            FlightListData(
                routeInfo = RouteInfo(
                    departureCity = departureCity,
                    arrivalCity = arrivalCity
                ),
                dateOptions = getDateOptions(selectedDate),
                promotionBanner = getPromotionBanner(),
                membershipInfo = getMembershipInfo(),
                filterTags = getFilterTags(),
                flightList = getFlightList(departureCity, arrivalCity, selectedDate),
                sortOptions = getSortOptions(),
                selectedDateIndex = 1
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override fun getDateOptions(baseDate: LocalDate): List<DateOption> {
        val today = LocalDate.now()
        val options = mutableListOf<DateOption>()
        
        for (i in 0..4) {
            val date = today.plusDays(i.toLong())
            val label = when (i) {
                0 -> "今天"
                1 -> "明天"
                2 -> "后天"
                else -> {
                    when (date.dayOfWeek.value) {
                        1 -> "周一"
                        2 -> "周二"
                        3 -> "周三"
                        4 -> "周四"
                        5 -> "周五"
                        6 -> "周六"
                        7 -> "周日"
                        else -> "周${date.dayOfWeek.value}"
                    }
                }
            }
            
            val displayDate = when (i) {
                0 -> date.dayOfMonth.toString()
                1 -> "${date.monthValue}-${date.dayOfMonth}"
                else -> date.dayOfMonth.toString()
            }
            
            val price = generatePriceForDate(date)
            
            options.add(
                DateOption(
                    id = "date_$i",
                    label = label,
                    date = date,
                    displayDate = displayDate,
                    price = price,
                    isSelected = i == 1, // Default select tomorrow
                    isToday = i == 0
                )
            )
        }
        
        return options
    }
    
    private fun generatePriceForDate(date: LocalDate): String {
        val basePrice = 350
        val dayVariation = (date.dayOfMonth % 7) * 20
        val weekendMultiplier = if (date.dayOfWeek.value >= 6) 1.2 else 1.0
        val finalPrice = ((basePrice + dayVariation) * weekendMultiplier).toInt()
        return "¥$finalPrice"
    }
    
    private fun getPromotionBanner(): FlightListPromotionBanner {
        return FlightListPromotionBanner(
            title = "机票次卡198元起",
            subtitle = "抢15元国内机票券",
            actionText = "免费抢"
        )
    }
    
    private fun getMembershipInfo(): MembershipInfo {
        return MembershipInfo(
            level = "普通会员",
            upgradeText = "升级解锁白银权益",
            benefits = "酒店免费取消 延迟退房 等"
        )
    }
    
    override fun getFilterTags(): List<FlightFilterTag> {
        return listOf(
            FlightFilterTag("filter_main", "筛选了", hasDropdown = true),
            FlightFilterTag("filter_afternoon", "下午出发"),
            FlightFilterTag("filter_large_aircraft", "大机型"),
            FlightFilterTag("filter_free_baggage", "免费托运行李"),
            FlightFilterTag("filter_hide_shared", "隐藏共享")
        )
    }
    
    override fun getFlightList(departureCity: String, arrivalCity: String, date: LocalDate): List<FlightItem> {
        return generateFlightsForRoute(departureCity, arrivalCity, date)
    }
    
    private fun generateFlightsForRoute(departureCity: String, arrivalCity: String, date: LocalDate): List<FlightItem> {
        val flights = mutableListOf<FlightItem>()
        val dateKey = "${departureCity}_${arrivalCity}"
        
        // 根据具体的城市组合和日期生成相应的航班
        when (dateKey) {
            "上海_北京" -> {
                flights.addAll(generateShanghaiToBeijingFlights(date))
            }
            "北京_上海" -> {
                flights.addAll(generateBeijingToShanghaiFlights(date))
            }
            "上海_广州" -> {
                flights.addAll(generateShanghaiToGuangzhouFlights(date))
            }
            "广州_上海" -> {
                flights.addAll(generateGuangzhouToShanghaiFlights(date))
            }
            "上海_深圳" -> {
                flights.addAll(generateShanghaiToShenzhenFlights(date))
            }
            "深圳_上海" -> {
                flights.addAll(generateShenzhenToShanghaiFlights(date))
            }
            "上海_成都" -> {
                flights.addAll(generateShanghaiToChengduFlights(date))
            }
            "成都_上海" -> {
                flights.addAll(generateChengduToShanghaiFlights(date))
            }
            "上海_杭州" -> {
                flights.addAll(generateShanghaiToHangzhouFlights(date))
            }
            "杭州_上海" -> {
                flights.addAll(generateHangzhouToShanghaiFlights(date))
            }
            "北京_广州" -> {
                flights.addAll(generateBeijingToGuangzhouFlights(date))
            }
            "广州_北京" -> {
                flights.addAll(generateGuangzhouToBeijingFlights(date))
            }
            "北京_深圳" -> {
                flights.addAll(generateBeijingToShenzhenFlights(date))
            }
            "深圳_北京" -> {
                flights.addAll(generateShenzhenToBeijingFlights(date))
            }
            "北京_成都" -> {
                flights.addAll(generateBeijingToChengduFlights(date))
            }
            "成都_北京" -> {
                flights.addAll(generateChengduToBeijingFlights(date))
            }
            "广州_深圳" -> {
                flights.addAll(generateGuangzhouToShenzhenFlights(date))
            }
            "深圳_广州" -> {
                flights.addAll(generateShenzhenToGuangzhouFlights(date))
            }
            "广州_成都" -> {
                flights.addAll(generateGuangzhouToChengduFlights(date))
            }
            "成都_广州" -> {
                flights.addAll(generateChengduToGuangzhouFlights(date))
            }
            "深圳_成都" -> {
                flights.addAll(generateShenzhenToChengduFlights(date))
            }
            "成都_深圳" -> {
                flights.addAll(generateChengduToShenzhenFlights(date))
            }
            else -> {
                // 默认航班，适用于其他城市组合
                flights.addAll(generateDefaultFlights(departureCity, arrivalCity, date))
            }
        }
        
        return flights
    }
    
    // 上海到北京航班
    private fun generateShanghaiToBeijingFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 330)
        return listOf(
            FlightItem(
                id = "SHA_BJS_${date}_1",
                departureTime = "07:20",
                arrivalTime = "09:40",
                departureAirport = "浦东T2",
                arrivalAirport = "大兴",
                airline = "吉祥",
                flightNumber = "HO1251",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 15}",
                discount = "限青老年票",
                tags = listOf("随行送机最高63折", "含共享"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_BJS_${date}_2",
                departureTime = "06:25",
                arrivalTime = "08:55",
                departureAirport = "浦东T2",
                arrivalAirport = "首都T2",
                airline = "海南航空",
                flightNumber = "HU7614",
                aircraftType = "波音737-800(中)",
                price = "¥${dayPrice + 20}",
                originalPrice = "¥${dayPrice + 50}",
                discount = "限18-22周岁票",
                tags = listOf("随行送机最高63折", "现金抵券第二季", "APP专享"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_BJS_${date}_3",
                departureTime = "08:10",
                arrivalTime = "10:35",
                departureAirport = "浦东T2",
                arrivalAirport = "大兴",
                airline = "南航",
                flightNumber = "CZ8890",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice + 70}",
                originalPrice = "¥${dayPrice + 100}",
                discount = "经济舱2.5折",
                tags = listOf("领300里程", "绿色飞行奖里程"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_BJS_${date}_4",
                departureTime = "14:15",
                arrivalTime = "16:45",
                departureAirport = "虹桥T2",
                arrivalAirport = "首都T3",
                airline = "国航",
                flightNumber = "CA1502",
                aircraftType = "空客330(大)",
                price = "¥${dayPrice + 50}",
                originalPrice = "¥${dayPrice + 80}",
                discount = "经济舱3.2折",
                tags = listOf("含餐食", "宽体机"),
                hasWifi = true
            )
        )
    }
    
    // 北京到上海航班
    private fun generateBeijingToShanghaiFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 325)
        return listOf(
            FlightItem(
                id = "BJS_SHA_${date}_1",
                departureTime = "08:30",
                arrivalTime = "11:00",
                departureAirport = "首都T3",
                arrivalAirport = "浦东T2",
                airline = "国航",
                flightNumber = "CA1503",
                aircraftType = "空客330(大)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 25}",
                discount = "早鸟特价",
                tags = listOf("含餐食", "宽体机", "准点率高"),
                hasWifi = true
            ),
            FlightItem(
                id = "BJS_SHA_${date}_2",
                departureTime = "12:15",
                arrivalTime = "14:50",
                departureAirport = "大兴",
                arrivalAirport = "虹桥T2",
                airline = "东航",
                flightNumber = "MU5137",
                aircraftType = "波音737-800(中)",
                price = "¥${dayPrice + 15}",
                originalPrice = "¥${dayPrice + 45}",
                discount = "限时优惠",
                tags = listOf("随行送机", "会员积分2倍"),
                hasWifi = true
            ),
            FlightItem(
                id = "BJS_SHA_${date}_3",
                departureTime = "17:40",
                arrivalTime = "20:20",
                departureAirport = "首都T2",
                arrivalAirport = "浦东T1",
                airline = "春秋",
                flightNumber = "9C8529",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice - 20}",
                originalPrice = "¥${dayPrice + 10}",
                discount = "超值特价",
                tags = listOf("低价首选"),
                hasWifi = false
            )
        )
    }
    
    // 上海到广州航班
    private fun generateShanghaiToGuangzhouFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 280)
        return listOf(
            FlightItem(
                id = "SHA_CAN_${date}_1",
                departureTime = "09:15",
                arrivalTime = "12:05",
                departureAirport = "浦东T1",
                arrivalAirport = "白云T2",
                airline = "南航",
                flightNumber = "CZ3539",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 30}",
                discount = "南航特惠",
                tags = listOf("含餐食", "行李23kg"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_CAN_${date}_2",
                departureTime = "13:25",
                arrivalTime = "16:15",
                departureAirport = "虹桥T1",
                arrivalAirport = "白云T1",
                airline = "东航",
                flightNumber = "MU5187",
                aircraftType = "波音737(中)",
                price = "¥${dayPrice + 25}",
                originalPrice = "¥${dayPrice + 55}",
                discount = "会员专享",
                tags = listOf("东航优选", "积分兑换"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_CAN_${date}_3",
                departureTime = "19:10",
                arrivalTime = "21:55",
                departureAirport = "浦东T2",
                arrivalAirport = "白云T2",
                airline = "深航",
                flightNumber = "ZH9247",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice + 10}",
                originalPrice = "¥${dayPrice + 40}",
                discount = "晚班特价",
                tags = listOf("深航服务", "免费改签"),
                hasWifi = true
            )
        )
    }
    
    // 广州到上海航班
    private fun generateGuangzhouToShanghaiFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 275)
        return listOf(
            FlightItem(
                id = "CAN_SHA_${date}_1",
                departureTime = "07:45",
                arrivalTime = "10:35",
                departureAirport = "白云T1",
                arrivalAirport = "虹桥T1",
                airline = "南航",
                flightNumber = "CZ3540",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 20}",
                discount = "早鸟价",
                tags = listOf("含餐食", "准点保障"),
                hasWifi = true
            ),
            FlightItem(
                id = "CAN_SHA_${date}_2",
                departureTime = "16:30",
                arrivalTime = "19:25",
                departureAirport = "白云T2",
                arrivalAirport = "浦东T1",
                airline = "厦航",
                flightNumber = "MF8115",
                aircraftType = "波音737-800(中)",
                price = "¥${dayPrice + 30}",
                originalPrice = "¥${dayPrice + 60}",
                discount = "厦航优选",
                tags = listOf("白鹭常客", "服务优质"),
                hasWifi = true
            )
        )
    }
    
    // 上海到深圳航班
    private fun generateShanghaiToShenzhenFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 290)
        return listOf(
            FlightItem(
                id = "SHA_SZX_${date}_1",
                departureTime = "08:40",
                arrivalTime = "11:35",
                departureAirport = "浦东T1",
                arrivalAirport = "宝安T3",
                airline = "深航",
                flightNumber = "ZH9685",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 25}",
                discount = "深航直飞",
                tags = listOf("深圳本土", "服务贴心"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_SZX_${date}_2",
                departureTime = "15:20",
                arrivalTime = "18:15",
                departureAirport = "虹桥T2",
                arrivalAirport = "宝安T3",
                airline = "东航",
                flightNumber = "MU5395",
                aircraftType = "波音737(中)",
                price = "¥${dayPrice + 15}",
                originalPrice = "¥${dayPrice + 45}",
                discount = "下午特惠",
                tags = listOf("东航优选", "里程累积"),
                hasWifi = true
            )
        )
    }
    
    // 深圳到上海航班
    private fun generateShenzhenToShanghaiFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 285)
        return listOf(
            FlightItem(
                id = "SZX_SHA_${date}_1",
                departureTime = "09:50",
                arrivalTime = "12:45",
                departureAirport = "宝安T3",
                arrivalAirport = "浦东T2",
                airline = "深航",
                flightNumber = "ZH9686",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 20}",
                discount = "深航优惠",
                tags = listOf("准点率高", "服务周到"),
                hasWifi = true
            ),
            FlightItem(
                id = "SZX_SHA_${date}_2",
                departureTime = "20:10",
                arrivalTime = "23:05",
                departureAirport = "宝安T3",
                arrivalAirport = "虹桥T1",
                airline = "春秋",
                flightNumber = "9C8747",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice - 30}",
                originalPrice = "¥${dayPrice}",
                discount = "晚班低价",
                tags = listOf("超值选择"),
                hasWifi = false
            )
        )
    }
    
    // 上海到成都航班
    private fun generateShanghaiToChengduFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 320)
        return listOf(
            FlightItem(
                id = "SHA_CTU_${date}_1",
                departureTime = "11:30",
                arrivalTime = "14:25",
                departureAirport = "浦东T2",
                arrivalAirport = "天府T1",
                airline = "川航",
                flightNumber = "3U8951",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 40}",
                discount = "川航特价",
                tags = listOf("川航服务", "熊猫主题"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_CTU_${date}_2",
                departureTime = "18:45",
                arrivalTime = "21:40",
                departureAirport = "虹桥T2",
                arrivalAirport = "双流T2",
                airline = "国航",
                flightNumber = "CA4503",
                aircraftType = "波音737-800(中)",
                price = "¥${dayPrice + 20}",
                originalPrice = "¥${dayPrice + 50}",
                discount = "国航优选",
                tags = listOf("含餐食", "凤凰知音"),
                hasWifi = true
            )
        )
    }
    
    // 成都到上海航班
    private fun generateChengduToShanghaiFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 315)
        return listOf(
            FlightItem(
                id = "CTU_SHA_${date}_1",
                departureTime = "08:15",
                arrivalTime = "11:10",
                departureAirport = "天府T1",
                arrivalAirport = "浦东T1",
                airline = "川航",
                flightNumber = "3U8952",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 35}",
                discount = "川航早班",
                tags = listOf("熊猫餐食", "准点保障"),
                hasWifi = true
            ),
            FlightItem(
                id = "CTU_SHA_${date}_2",
                departureTime = "16:25",
                arrivalTime = "19:20",
                departureAirport = "双流T1",
                arrivalAirport = "虹桥T1",
                airline = "东航",
                flightNumber = "MU5467",
                aircraftType = "波音737(中)",
                price = "¥${dayPrice + 25}",
                originalPrice = "¥${dayPrice + 55}",
                discount = "东航特惠",
                tags = listOf("东航优选", "积分双倍"),
                hasWifi = true
            )
        )
    }
    
    // 上海到杭州航班 (短程航线)
    private fun generateShanghaiToHangzhouFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 180)
        return listOf(
            FlightItem(
                id = "SHA_HGH_${date}_1",
                departureTime = "09:30",
                arrivalTime = "10:20",
                departureAirport = "虹桥T2",
                arrivalAirport = "萧山T3",
                airline = "吉祥",
                flightNumber = "HO1039",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 15}",
                discount = "短程特价",
                tags = listOf("快速直达", "公务出行"),
                hasWifi = true
            ),
            FlightItem(
                id = "SHA_HGH_${date}_2",
                departureTime = "17:15",
                arrivalTime = "18:05",
                departureAirport = "虹桥T1",
                arrivalAirport = "萧山T1",
                airline = "春秋",
                flightNumber = "9C8863",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice - 20}",
                originalPrice = "¥${dayPrice}",
                discount = "超值价",
                tags = listOf("经济实惠"),
                hasWifi = false
            )
        )
    }
    
    // 杭州到上海航班
    private fun generateHangzhouToShanghaiFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 175)
        return listOf(
            FlightItem(
                id = "HGH_SHA_${date}_1",
                departureTime = "11:45",
                arrivalTime = "12:35",
                departureAirport = "萧山T3",
                arrivalAirport = "虹桥T2",
                airline = "吉祥",
                flightNumber = "HO1040",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 10}",
                discount = "商务优选",
                tags = listOf("快速便捷", "商务舱升级"),
                hasWifi = true
            )
        )
    }
    
    // 其他城市组合的默认航班
    private fun generateDefaultFlights(departureCity: String, arrivalCity: String, date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 350)
        val departureAirport = getAirportForCity(departureCity)
        val arrivalAirport = getAirportForCity(arrivalCity)
        
        return listOf(
            FlightItem(
                id = "${departureCity}_${arrivalCity}_${date}_1",
                departureTime = "09:15",
                arrivalTime = "11:35",
                departureAirport = departureAirport,
                arrivalAirport = arrivalAirport,
                airline = "东航",
                flightNumber = "MU${(1000..9999).random()}",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 30}",
                discount = "特价优惠",
                tags = listOf("东航优选"),
                hasWifi = true
            ),
            FlightItem(
                id = "${departureCity}_${arrivalCity}_${date}_2",
                departureTime = "14:20",
                arrivalTime = "16:45",
                departureAirport = departureAirport,
                arrivalAirport = arrivalAirport,
                airline = "南航",
                flightNumber = "CZ${(1000..9999).random()}",
                aircraftType = "波音737(中)",
                price = "¥${dayPrice + 20}",
                originalPrice = "¥${dayPrice + 50}",
                discount = "南航特惠",
                tags = listOf("南航服务"),
                hasWifi = true
            )
        )
    }
    
    // 根据日期计算价格（考虑周末、工作日等因素）
    private fun calculateDayPrice(date: LocalDate, basePrice: Int): Int {
        var finalPrice = basePrice
        
        // 日期在10月20-25号范围内的价格调整
        when (date.dayOfMonth) {
            20 -> finalPrice += 0  // 周日，正常价格
            21 -> finalPrice -= 10 // 周一，工作日优惠
            22 -> finalPrice -= 15 // 周二，工作日最优惠
            23 -> finalPrice -= 5  // 周三，工作日优惠
            24 -> finalPrice += 10 // 周四，接近周末
            25 -> finalPrice += 20 // 周五，周末前涨价
        }
        
        // 确保价格不低于基础价格的80%
        return maxOf(finalPrice, (basePrice * 0.8).toInt())
    }
    
    // 根据城市获取机场代码
    private fun getAirportForCity(cityName: String): String {
        return when (cityName) {
            "上海" -> "浦东T2"
            "北京" -> "首都T3"
            "广州" -> "白云T2"
            "深圳" -> "宝安T3"
            "成都" -> "天府T1"
            "杭州" -> "萧山T3"
            else -> "${cityName}机场"
        }
    }
    
    // 北京到广州航班
    private fun generateBeijingToGuangzhouFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 380)
        return listOf(
            FlightItem(
                id = "BJS_CAN_${date}_1",
                departureTime = "10:30",
                arrivalTime = "14:15",
                departureAirport = "首都T3",
                arrivalAirport = "白云T2",
                airline = "南航",
                flightNumber = "CZ3101",
                aircraftType = "空客330(大)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 40}",
                discount = "南航主场",
                tags = listOf("宽体机", "含餐食", "南航明珠"),
                hasWifi = true
            ),
            FlightItem(
                id = "BJS_CAN_${date}_2",
                departureTime = "15:45",
                arrivalTime = "19:30",
                departureAirport = "大兴",
                arrivalAirport = "白云T1",
                airline = "国航",
                flightNumber = "CA1301",
                aircraftType = "波音737-800(中)",
                price = "¥${dayPrice + 25}",
                originalPrice = "¥${dayPrice + 65}",
                discount = "国航优选",
                tags = listOf("含餐食", "凤凰知音"),
                hasWifi = true
            )
        )
    }
    
    // 广州到北京航班
    private fun generateGuangzhouToBeijingFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 375)
        return listOf(
            FlightItem(
                id = "CAN_BJS_${date}_1",
                departureTime = "08:20",
                arrivalTime = "12:05",
                departureAirport = "白云T2",
                arrivalAirport = "首都T3",
                airline = "南航",
                flightNumber = "CZ3102",
                aircraftType = "空客330(大)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 35}",
                discount = "早班特惠",
                tags = listOf("宽体机", "准点保障"),
                hasWifi = true
            )
        )
    }
    
    // 北京到深圳航班
    private fun generateBeijingToShenzhenFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 390)
        return listOf(
            FlightItem(
                id = "BJS_SZX_${date}_1",
                departureTime = "12:40",
                arrivalTime = "16:25",
                departureAirport = "首都T2",
                arrivalAirport = "宝安T3",
                airline = "深航",
                flightNumber = "ZH9897",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 30}",
                discount = "深航直飞",
                tags = listOf("深航优质服务"),
                hasWifi = true
            )
        )
    }
    
    // 深圳到北京航班
    private fun generateShenzhenToBeijingFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 385)
        return listOf(
            FlightItem(
                id = "SZX_BJS_${date}_1",
                departureTime = "07:50",
                arrivalTime = "11:35",
                departureAirport = "宝安T3",
                arrivalAirport = "首都T2",
                airline = "深航",
                flightNumber = "ZH9898",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 25}",
                discount = "早鸟价",
                tags = listOf("准点率高", "深航服务"),
                hasWifi = true
            )
        )
    }
    
    // 北京到成都航班
    private fun generateBeijingToChengduFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 340)
        return listOf(
            FlightItem(
                id = "BJS_CTU_${date}_1",
                departureTime = "13:15",
                arrivalTime = "16:30",
                departureAirport = "首都T3",
                arrivalAirport = "天府T1",
                airline = "川航",
                flightNumber = "3U8881",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 35}",
                discount = "川航特色",
                tags = listOf("熊猫服务", "川航优选"),
                hasWifi = true
            )
        )
    }
    
    // 成都到北京航班
    private fun generateChengduToBeijingFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 335)
        return listOf(
            FlightItem(
                id = "CTU_BJS_${date}_1",
                departureTime = "09:40",
                arrivalTime = "12:55",
                departureAirport = "天府T1",
                arrivalAirport = "首都T3",
                airline = "川航",
                flightNumber = "3U8882",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 30}",
                discount = "川航优惠",
                tags = listOf("熊猫主题", "川味服务"),
                hasWifi = true
            )
        )
    }
    
    // 广州到深圳航班（短程）
    private fun generateGuangzhouToShenzhenFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 120)
        return listOf(
            FlightItem(
                id = "CAN_SZX_${date}_1",
                departureTime = "14:20",
                arrivalTime = "15:00",
                departureAirport = "白云T1",
                arrivalAirport = "宝安T3",
                airline = "深航",
                flightNumber = "ZH9311",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 10}",
                discount = "短程特价",
                tags = listOf("快速便捷"),
                hasWifi = true
            )
        )
    }
    
    // 深圳到广州航班（短程）
    private fun generateShenzhenToGuangzhouFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 115)
        return listOf(
            FlightItem(
                id = "SZX_CAN_${date}_1",
                departureTime = "16:30",
                arrivalTime = "17:10",
                departureAirport = "宝安T3",
                arrivalAirport = "白云T1",
                airline = "深航",
                flightNumber = "ZH9312",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 10}",
                discount = "短程优惠",
                tags = listOf("珠三角快线"),
                hasWifi = true
            )
        )
    }
    
    // 广州到成都航班
    private fun generateGuangzhouToChengduFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 310)
        return listOf(
            FlightItem(
                id = "CAN_CTU_${date}_1",
                departureTime = "11:25",
                arrivalTime = "13:55",
                departureAirport = "白云T2",
                arrivalAirport = "天府T1",
                airline = "川航",
                flightNumber = "3U8731",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 40}",
                discount = "川航特色",
                tags = listOf("熊猫餐食", "川航服务"),
                hasWifi = true
            )
        )
    }
    
    // 成都到广州航班
    private fun generateChengduToGuangzhouFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 305)
        return listOf(
            FlightItem(
                id = "CTU_CAN_${date}_1",
                departureTime = "15:10",
                arrivalTime = "17:40",
                departureAirport = "双流T1",
                arrivalAirport = "白云T2",
                airline = "南航",
                flightNumber = "CZ3411",
                aircraftType = "波音737-800(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 35}",
                discount = "南航优选",
                tags = listOf("南航服务", "明珠会员"),
                hasWifi = true
            )
        )
    }
    
    // 深圳到成都航班
    private fun generateShenzhenToChengduFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 325)
        return listOf(
            FlightItem(
                id = "SZX_CTU_${date}_1",
                departureTime = "18:30",
                arrivalTime = "21:15",
                departureAirport = "宝安T3",
                arrivalAirport = "天府T1",
                airline = "川航",
                flightNumber = "3U8695",
                aircraftType = "空客320(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 30}",
                discount = "晚班特惠",
                tags = listOf("川航优质", "熊猫主题"),
                hasWifi = true
            )
        )
    }
    
    // 成都到深圳航班
    private fun generateChengduToShenzhenFlights(date: LocalDate): List<FlightItem> {
        val dayPrice = calculateDayPrice(date, 320)
        return listOf(
            FlightItem(
                id = "CTU_SZX_${date}_1",
                departureTime = "10:15",
                arrivalTime = "13:00",
                departureAirport = "双流T2",
                arrivalAirport = "宝安T3",
                airline = "深航",
                flightNumber = "ZH9635",
                aircraftType = "空客321(中)",
                price = "¥${dayPrice}",
                originalPrice = "¥${dayPrice + 25}",
                discount = "深航直飞",
                tags = listOf("深航服务", "准点保障"),
                hasWifi = true
            )
        )
    }
    
    override fun getSortOptions(): List<SortOption> {
        return listOf(
            SortOption("sort_recommend", "推荐排序", "👍", isSelected = true),
            SortOption("sort_direct", "直飞优先", "✈️"),
            SortOption("sort_time", "时间排序", "⏰"),
            SortOption("sort_price", "价格排序", "💰")
        )
    }
    
    override fun applyFilters(flights: List<FlightItem>, activeFilters: List<String>): List<FlightItem> {
        var filteredFlights = flights
        
        if (activeFilters.contains("filter_afternoon")) {
            filteredFlights = filteredFlights.filter { 
                val hour = it.departureTime.split(":")[0].toInt()
                hour >= 12
            }
        }
        
        if (activeFilters.contains("filter_large_aircraft")) {
            filteredFlights = filteredFlights.filter { 
                it.aircraftType.contains("321") || it.aircraftType.contains("330") || it.aircraftType.contains("777")
            }
        }
        
        if (activeFilters.contains("filter_free_baggage")) {
            filteredFlights = filteredFlights.filter { 
                !it.tags.any { tag -> tag.contains("行李") }
            }
        }
        
        if (activeFilters.contains("filter_hide_shared")) {
            filteredFlights = filteredFlights.filter { 
                !it.tags.any { tag -> tag.contains("共享") }
            }
        }
        
        return filteredFlights
    }
    
    override fun sortFlights(flights: List<FlightItem>, sortBy: String): List<FlightItem> {
        return when (sortBy) {
            "sort_price" -> flights.sortedBy { it.price.replace("¥", "").toInt() }
            "sort_time" -> flights.sortedBy { it.departureTime }
            "sort_direct" -> flights.sortedByDescending { !it.tags.any { tag -> tag.contains("共享") } }
            else -> flights // Keep original order for "recommend"
        }
    }
    
    override fun saveFlightSelection(flightId: String) {
        val selectionData = mapOf(
            "action" to "flight_selected",
            "timestamp" to System.currentTimeMillis(),
            "flight_id" to flightId,
            "page" to "flight_list"
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_flight_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun saveDateSelection(dateId: String) {
        sharedPreferences.edit()
            .putString("selected_date_id", dateId)
            .apply()
    }
    
    override fun saveFilterPreferences(activeFilters: List<String>) {
        val filtersJson = gson.toJson(activeFilters)
        sharedPreferences.edit()
            .putString("active_filters", filtersJson)
            .apply()
    }
    
    override fun saveSortPreference(sortId: String) {
        sharedPreferences.edit()
            .putString("sort_preference", sortId)
            .apply()
    }
}