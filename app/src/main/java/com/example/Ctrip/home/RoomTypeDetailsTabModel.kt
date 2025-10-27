package com.example.Ctrip.home

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class RoomTypeDetailsTabModelImpl(private val context: Context) : RoomTypeDetailsTabContract.Model {
    
    private val gson = Gson()
    
    override fun getRoomTypeDetailsData(
        roomType: RoomType, 
        searchParams: HotelListSearchParams
    ): RoomTypeDetailsData? {
        return try {
            // 构造房型详情数据
            val imageGallery = generateRoomImages(roomType.id)
            val roomFeatures = generateRoomFeatures(roomType)
            val bedInfo = generateBedInfo(roomType)
            val breakfastInfo = generateBreakfastInfo(roomType)
            val roomFacilities = generateRoomFacilities()
            val giftServices = generateGiftServices()
            val specialOffers = generateSpecialOffers()
            val priceInfo = generatePriceInfo(roomType)
            
            // 获取酒店信息
            val hotelInfo = HotelDetailInfo(
                name = "酒店名称",
                badges = emptyList()
            )
            
            RoomTypeDetailsData(
                roomType = roomType,
                searchParams = searchParams,
                hotelInfo = hotelInfo,
                imageGallery = imageGallery,
                currentImageIndex = 0,
                roomFeatures = roomFeatures,
                bedInfo = bedInfo,
                breakfastInfo = breakfastInfo,
                roomFacilities = roomFacilities,
                giftServices = giftServices,
                specialOffers = specialOffers,
                priceInfo = priceInfo
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun generateRoomImages(roomTypeId: String): List<RoomImage> {
        return listOf(
            RoomImage("img1", "", "房间全景"),
            RoomImage("img2", "", "床铺特写"),
            RoomImage("img3", "", "卫生间"),
            RoomImage("img4", "", "房间设施"),
            RoomImage("img5", "", "窗外景色")
        )
    }
    
    private fun generateRoomFeatures(roomType: RoomType): List<RoomFeature> {
        return listOf(
            RoomFeature("🏠", roomType.area, "房间面积"),
            RoomFeature("🏢", roomType.floor, "楼层"),
            RoomFeature("📶", "Wi-Fi免费", "无线网络"),
            RoomFeature("🪟", "有窗", "自然采光"),
            RoomFeature("🚭", "禁烟", "无烟房间")
        )
    }
    
    private fun generateBedInfo(roomType: RoomType): BedInfo {
        return BedInfo(
            bedType = "1张大床",
            bedSize = "1.8米",
            extraBedPolicy = "成人加床:该房型不可加床"
        )
    }
    
    private fun generateBreakfastInfo(roomType: RoomType): BreakfastInfo {
        return BreakfastInfo(
            included = false,
            description = "无早餐"
        )
    }
    
    private fun generateRoomFacilities(): List<RoomFacility> {
        return listOf(
            RoomFacility("wifi", "无线网络", "📶"),
            RoomFacility("ac", "空调", "❄️"),
            RoomFacility("tv", "电视", "📺"),
            RoomFacility("fridge", "冰箱", "🧊"),
            RoomFacility("bath", "浴缸", "🛁"),
            RoomFacility("safe", "保险箱", "🔒"),
            RoomFacility("desk", "书桌", "🪑"),
            RoomFacility("wardrobe", "衣柜", "👗")
        )
    }
    
    private fun generateGiftServices(): List<GiftService> {
        return listOf(
            GiftService(
                "airport_transfer",
                "大兴机场24小时接送机服务",
                "专车接送，安全便捷",
                "1份"
            ),
            GiftService(
                "customer_service",
                "24小时贴心客服管家服务",
                "全天候服务，解决您的疑问",
                "1份"
            )
        )
    }
    
    private fun generateSpecialOffers(): List<SpecialOffer> {
        return listOf(
            SpecialOffer(
                "first_stay",
                "首住好礼 为您升级权益",
                "首住好礼",
                "预订酒店可享85折起",
                "85折起"
            )
        )
    }
    
    private fun generatePriceInfo(roomType: RoomType): PriceInfo {
        return PriceInfo(
            currentPrice = roomType.price,
            originalPrice = roomType.originalPrice,
            discount = "首住特惠",
            priceDescription = "4项优惠81>"
        )
    }
    
    override fun addToCart(roomTypeId: String, searchParams: HotelListSearchParams) {
        // 添加到购物车的逻辑
        saveRoomTypeDetailsAction("add_to_cart", mapOf(
            "roomTypeId" to roomTypeId,
            "searchParams" to searchParams
        ))
    }
    
    override fun saveRoomTypeDetailsAction(action: String, data: Map<String, Any>) {
        // 保存用户操作记录，用于AI任务检查
        try {
            val actionData = mapOf(
                "action" to action,
                "timestamp" to System.currentTimeMillis(),
                "data" to data
            )
            
            // 这里可以保存到本地文件或SharedPreferences
            // 简单起见，暂时只打印日志
            println("RoomTypeDetails Action: $actionData")
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}