package com.example.Ctrip.home

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import com.example.Ctrip.utils.DateUtils

interface HotelBookingTabModel {
    fun getHotelBookingData(): HotelBookingData?
    fun updateSearchParams(params: HotelSearchParams)
    fun saveSearchAction(searchParams: HotelSearchParams)
}

class HotelBookingTabModelImpl(private val context: Context) : HotelBookingTabModel {
    
    private var currentSearchParams = HotelSearchParams(
        city = "北京",
        checkInDate = DateUtils.getCurrentDate(),
        checkOutDate = DateUtils.getTomorrowDate(),
        roomCount = 1,
        adultCount = 1,
        childCount = 0
    )
    
    override fun getHotelBookingData(): HotelBookingData? {
        return try {
            HotelBookingData(
                promotionBanner = HotelPromotionBanner(
                    title = "11.11值得囤",
                    subtitle = "酒店预售 6折起",
                    backgroundColor = "#4A90E2"
                ),
                hotelCategories = listOf(
                    HotelCategory("domestic", "国内", true),
                    HotelCategory("overseas", "海外", false),
                    HotelCategory("hourly", "钟点房", false),
                    HotelCategory("homestay", "民宿", false)
                ),
                searchParams = currentSearchParams,
                quickSearchTags = listOf("双床房", "天安门广场", "返10倍积分", "北京南站", "朝阳"),
                benefitSections = listOf(
                    BenefitSection(
                        title = "首住好礼",
                        discountText = "85折起",
                        benefitText = "标有首住特惠的房型可用",
                        backgroundColor = "#FF8A65"
                    ),
                    BenefitSection(
                        title = "4项权益",
                        discountText = "免费取消、延迟退房",
                        benefitText = "免费早餐、房型升级",
                        backgroundColor = "#E1BEE7"
                    )
                ),
                bottomSections = listOf(
                    BottomSection("reputation", "口碑榜", "城市精选", "🏆"),
                    BottomSection("special", "特价套餐", "随时退", "🎁"),
                    BottomSection("lowPrice", "超值低价", "7折起", "💰")
                )
            )
        } catch (e: Exception) {
            null
        }
    }
    
    override fun updateSearchParams(params: HotelSearchParams) {
        currentSearchParams = params
    }
    
    override fun saveSearchAction(searchParams: HotelSearchParams) {
        // Save search action for AI analysis
        try {
            val sharedPrefs = context.getSharedPreferences("hotel_actions", Context.MODE_PRIVATE)
            val searchHistory = sharedPrefs.getStringSet("search_history", mutableSetOf()) ?: mutableSetOf()
            
            val searchRecord = mapOf(
                "timestamp" to System.currentTimeMillis(),
                "city" to searchParams.city,
                "checkInDate" to searchParams.checkInDate.toString(),
                "checkOutDate" to searchParams.checkOutDate.toString(),
                "roomCount" to searchParams.roomCount,
                "adultCount" to searchParams.adultCount,
                "childCount" to searchParams.childCount
            )
            
            searchHistory.add(Gson().toJson(searchRecord))
            sharedPrefs.edit().putStringSet("search_history", searchHistory).apply()
        } catch (e: Exception) {
            // Log error
        }
    }
}