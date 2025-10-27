package com.example.Ctrip.home

import android.content.Context
import com.example.Ctrip.model.*
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.ZoneId
import java.text.SimpleDateFormat
import java.util.*

class InfoConfirmationTabModelImpl(private val context: Context) : InfoConfirmationTabContract.Model {
    
    private val gson = Gson()
    
    override fun getInfoConfirmationData(roomType: RoomType, searchParams: HotelListSearchParams): InfoConfirmationData? {
        return try {
            println("Debug: Loading confirmation data for roomType.id = ${roomType.id}")
            
            // Extract hotel ID from room type ID (format: "room_bj_001_1" -> "hotel_001")
            val hotelId = when {
                roomType.id.contains("_bj_001_") -> "hotel_001"
                roomType.id.contains("_sh_002_") -> "hotel_002"  
                roomType.id.contains("_gz_003_") -> "hotel_003"
                roomType.id.contains("_sz_004_") -> "hotel_004"
                roomType.id.contains("_hz_005_") -> "hotel_005"
                else -> "hotel_001" // Default fallback
            }
            
            println("Debug: Extracted hotelId = $hotelId")
            
            // Load hotel info from assets
            val hotelInfo = loadHotelInfo(hotelId)
            if (hotelInfo == null) {
                println("Debug: Failed to load hotel info for hotelId = $hotelId")
                return null
            }
            
            println("Debug: Successfully loaded hotel info: ${hotelInfo.name}")
            
            // Calculate dates
            val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val checkInDate = formatter.parse(searchParams.checkInDate.toString())
            val checkOutDate = formatter.parse(searchParams.checkOutDate.toString())
            val nights = ((checkOutDate?.time ?: 0) - (checkInDate?.time ?: 0)) / (1000 * 60 * 60 * 24)
            
            // Format date range
            val checkInCalendar = Calendar.getInstance().apply { time = checkInDate ?: Date() }
            val checkOutCalendar = Calendar.getInstance().apply { time = checkOutDate ?: Date() }
            val checkInStr = "${checkInCalendar.get(Calendar.MONTH) + 1}月${checkInCalendar.get(Calendar.DAY_OF_MONTH)}日今天"
            val checkOutStr = "${checkOutCalendar.get(Calendar.MONTH) + 1}月${checkOutCalendar.get(Calendar.DAY_OF_MONTH)}日明天"
            val dateRange = "$checkInStr — $checkOutStr ${nights}晚"
            
            InfoConfirmationData(
                hotelName = "${hotelInfo.name}(${hotelInfo.address})",
                roomType = roomType,
                searchParams = searchParams,
                checkInInfo = CheckInInfo(
                    dateRange = dateRange,
                    checkInTime = "14:00后入住",
                    checkOutTime = "12:00前退房",
                    nights = nights.toInt()
                ),
                cancelPolicy = "10月23日14:00前免费取消",
                roomCount = 1,
                guestName = "张凯文",
                contactPhone = "138 9527 8866",
                priceBreakdown = PriceBreakdown(
                    originalPrice = roomType.price + 81, // Add discount for demo
                    finalPrice = roomType.price,
                    discountAmount = 81,
                    description = "新人价"
                ),
                promotions = listOf(
                    Promotion("1", "促销优惠", 62, "3项优惠共减¥62"),
                    Promotion("2", "折扣券", 19, "折扣券减¥19")
                ),
                coupons = listOf(
                    Coupon("1", "折扣券", 19, "新人专享折扣券")
                ),
                gifts = listOf(
                    Gift("1", "大兴机场24小时接送机服务", "免费专车接送服务")
                ),
                benefits = listOf(
                    Benefit("1", "首住权益·1项", "延迟退房至14:00")
                ),
                pointsInfo = PointsInfo(
                    earnPoints = 520,
                    value = 5,
                    description = "离店赚10倍积分"
                ),
                urgencyMessage = "当前房源仅剩2间，赶紧下单吧！"
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    override fun updateRoomCount(roomTypeId: String, count: Int) {
        // Save room count to local storage for booking state
        val bookingData = mapOf(
            "roomTypeId" to roomTypeId,
            "roomCount" to count,
            "timestamp" to System.currentTimeMillis()
        )
        saveBookingInfo(bookingData)
    }
    
    override fun updateGuestInfo(roomTypeId: String, guestName: String, contactPhone: String) {
        // Save guest info to local storage
        val guestData = mapOf(
            "roomTypeId" to roomTypeId,
            "guestName" to guestName,
            "contactPhone" to contactPhone,
            "timestamp" to System.currentTimeMillis()
        )
        saveBookingInfo(guestData)
    }
    
    override fun saveBookingInfo(data: Map<String, Any>) {
        try {
            val sharedPreferences = context.getSharedPreferences("booking_info", Context.MODE_PRIVATE)
            val editor = sharedPreferences.edit()
            
            data.forEach { (key, value) ->
                when (value) {
                    is String -> editor.putString(key, value)
                    is Int -> editor.putInt(key, value)
                    is Long -> editor.putLong(key, value)
                    is Boolean -> editor.putBoolean(key, value)
                    else -> editor.putString(key, value.toString())
                }
            }
            
            editor.apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    
    private fun loadHotelInfo(hotelId: String): HotelInfo? {
        return try {
            val inputStream = context.assets.open("data/hotels.json")
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            
            val hotelType = object : TypeToken<List<HotelInfo>>() {}.type
            val hotels: List<HotelInfo> = gson.fromJson(jsonString, hotelType)
            
            hotels.find { it.hotelId == hotelId }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}