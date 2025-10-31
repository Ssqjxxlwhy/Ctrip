package com.example.Ctrip.home

import android.content.Context
import com.google.gson.Gson
import java.time.LocalDate
import com.example.Ctrip.utils.DateUtils

interface FlightBookingTabModel {
    fun getFlightBookingData(): FlightBookingData?
    fun updateSearchParams(params: FlightSearchParams)
    fun saveSearchAction(searchParams: FlightSearchParams)
}

class FlightBookingTabModelImpl(private val context: Context) : FlightBookingTabModel {
    
    private var currentSearchParams = FlightSearchParams(
        departureCity = "上海",
        arrivalCity = "北京",
        departureDate = DateUtils.getTomorrowDate(), // 明天
        passengerInfo = PassengerInfo(
            adultCount = 1,
            childCount = 0,
            infantCount = 0
        ),
        selectedCabin = "economy"
    )
    
    override fun getFlightBookingData(): FlightBookingData? {
        return try {
            FlightBookingData(
                promotionBanner = FlightPromotionBanner(
                    title = "双人同行人均460元起",
                    subtitle = "全日空航空超级品牌日",
                    backgroundColor = "#FF6600"
                ),
                transportTabs = listOf(
                    TransportTab("flight", "机票", true),
                    TransportTab("special_flight", "特价机票", false, true),
                    TransportTab("train", "火车票", false),
                    TransportTab("bus_ship", "汽车·船票", false)
                ),
                tripTypeTabs = listOf(
                    TripTypeTab("oneway", "单程", true),
                    TripTypeTab("roundtrip", "往返", false),
                    TripTypeTab("multi", "多程", false)
                ),
                searchParams = currentSearchParams,
                cabinTypes = listOf(
                    CabinType("economy", "经济舱", currentSearchParams.selectedCabin == "economy"),
                    CabinType("business", "公务/头等舱", currentSearchParams.selectedCabin == "business")
                ),
                serviceFeatures = listOf(
                    ServiceFeature("low_price", "搜全国低价", "🌏"),
                    ServiceFeature("price_alert", "低价提醒", "🔔"),
                    ServiceFeature("student", "学生特权", "🎓"),
                    ServiceFeature("visa_guide", "出入境攻略", "📖"),
                    ServiceFeature("expand", "展开全部", "📋")
                ),
                membershipSections = listOf(
                    MembershipSection(
                        "regular_member",
                        "普通会员",
                        "0积分待使用",
                        "#4A90E2",
                        "#FFFFFF"
                    ),
                    MembershipSection(
                        "wechat_benefits",
                        "企微福利",
                        "关注特价资讯",
                        "#FF9800",
                        "#FFFFFF"
                    ),
                    MembershipSection(
                        "coupon_center",
                        "领券中心",
                        "每日好券",
                        "#E91E63",
                        "#FFFFFF"
                    )
                ),
                inspirationSections = listOf(
                    InspirationSection(
                        "business_lowprice",
                        "公务/头等舱低价榜",
                        "17万人正在查看榜单",
                        "business_class.jpg"
                    ),
                    InspirationSection(
                        "family_travel",
                        "国内亲子榜",
                        "5月的地中海榜",
                        "family_travel.jpg"
                    )
                ),
                bottomFeatures = listOf(
                    BottomFeature("flight_assistant", "航班助手", "✈️"),
                    BottomFeature("benefits", "我的权益", "💎"),
                    BottomFeature("charter", "定制包机", "🛩️"),
                    BottomFeature("invoice", "报销凭证", "📋"),
                    BottomFeature("orders", "我的订单", "📝")
                )
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override fun updateSearchParams(params: FlightSearchParams) {
        currentSearchParams = params
    }
    
    override fun saveSearchAction(searchParams: FlightSearchParams) {
        try {
            val sharedPrefs = context.getSharedPreferences("flight_actions", Context.MODE_PRIVATE)
            val searchHistory = sharedPrefs.getStringSet("search_history", mutableSetOf()) ?: mutableSetOf()
            
            val searchRecord = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "departureCity" to searchParams.departureCity,
                "arrivalCity" to searchParams.arrivalCity,
                "departureDate" to searchParams.departureDate.toString(),
                "returnDate" to searchParams.returnDate?.toString(),
                "adultCount" to searchParams.passengerInfo.adultCount,
                "childCount" to searchParams.passengerInfo.childCount,
                "infantCount" to searchParams.passengerInfo.infantCount,
                "cabin" to searchParams.selectedCabin
            )
            
            searchHistory.add(Gson().toJson(searchRecord))
            sharedPrefs.edit().putStringSet("search_history", searchHistory).apply()
        } catch (e: Exception) {
            // Log error
        }
    }
}