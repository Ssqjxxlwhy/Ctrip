package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.LocalDate
import com.example.Ctrip.utils.DateUtils

interface HotelDetailTabModel : HotelDetailTabContract.Model

class HotelDetailTabModelImpl(private val context: Context) : HotelDetailTabModel {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("hotel_detail_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // 预设的酒店详情数据
    private val hotelDetailsMap = mapOf(
        // 北京酒店详情
        "hotel_bj_001" to createHotelDetail(
            hotelId = "hotel_bj_001",
            name = "北京一家人民宿(大兴国际机场店)",
            badges = listOf(
                HotelBadge("vip", "优享会", "vip"),
                HotelBadge("year", "2021年开业", "year")
            ),
            amenities = listOf(
                HotelAmenity("pickup", "✈️", "免费接送机"),
                HotelAmenity("parking", "🅿️", "免费停车"),
                HotelAmenity("family", "👨‍👩‍👧‍👦", "家庭房"),
                HotelAmenity("nosmoking", "🚭", "无烟楼层"),
                HotelAmenity("luggage", "🧳", "免费行李"),
                HotelAmenity("policy", "📋", "设施政策")
            ),
            rating = 4.7,
            reviewCount = 250,
            summary = "离大兴机场很近有接送机服务很方便",
            address = "大兴区礼贤镇东河村河坎村13号",
            distanceInfo = "距礼贤站驾车5.8公里",
            promotions = listOf(
                PromotionItem("first_stay", "首住特惠", "85折起", "first_stay", "#FF6B6B"),
                PromotionItem("autumn", "秋冬特惠", "最高减50", "seasonal", "#4A90E2"),
                PromotionItem("discount", "9折", "", "discount", "#32C832"),
                PromotionItem("coupon", "领券", "", "coupon", "#FF9500")
            ),
            roomTypes = listOf(
                RoomType(
                    id = "room_bj_001_1",
                    name = "精品大床间",
                    imageUrl = "room_bj_001_1.jpg",
                    description = "舒适床垫+纯棉布草+独立空调+通透明亮",
                    features = listOf("1张1.8米大床", "18-21㎡", "2人入住", "1层"),
                    bedInfo = "1张1.8米大床",
                    area = "18-21㎡",
                    maxGuests = 2,
                    floor = "1层",
                    breakfast = "无早餐",
                    cancellationPolicy = "入住当天14:00前可免费取消",
                    price = 104,
                    originalPrice = 185,
                    badge = "本店销量No.1"
                ),
                RoomType(
                    id = "room_bj_001_2",
                    name = "豪华双床房",
                    imageUrl = "room_bj_001_2.jpg",
                    description = "宽敞舒适+双床设计+商务设施",
                    features = listOf("2张1.5米大床", "25-28㎡", "4人入住", "2层"),
                    bedInfo = "2张1.5米大床",
                    area = "25-28㎡",
                    maxGuests = 4,
                    floor = "2层",
                    breakfast = "含早餐",
                    cancellationPolicy = "入住当天18:00前可免费取消",
                    price = 158,
                    originalPrice = 220
                ),
                RoomType(
                    id = "room_bj_001_3",
                    name = "家庭套房",
                    imageUrl = "room_bj_001_3.jpg",
                    description = "温馨家庭+独立客厅+亲子设施",
                    features = listOf("1张2米大床+1张儿童床", "35-40㎡", "5人入住", "3层"),
                    bedInfo = "1张2米大床+1张儿童床",
                    area = "35-40㎡",
                    maxGuests = 5,
                    floor = "3层",
                    breakfast = "含早餐",
                    cancellationPolicy = "入住前1天可免费取消",
                    price = 238,
                    originalPrice = 310
                )
            )
        ),
        
        "hotel_bj_002" to createHotelDetail(
            hotelId = "hotel_bj_002",
            name = "北京兰岩酒店(德胜门鼓楼大街地铁站店)",
            badges = listOf(
                HotelBadge("vip", "优享会", "vip"),
                HotelBadge("year", "2020年装修", "year")
            ),
            amenities = listOf(
                HotelAmenity("cinema", "🎬", "自营影音房"),
                HotelAmenity("laundry", "👕", "洗衣房"),
                HotelAmenity("service", "🛎️", "管家服务"),
                HotelAmenity("wifi", "📶", "无线接置"),
                HotelAmenity("metro", "🚇", "地铁直达"),
                HotelAmenity("policy", "📋", "设施政策")
            ),
            rating = 4.5,
            reviewCount = 2995,
            summary = "地理位置好，住宿环境干净卫生",
            address = "德胜门外大街26号",
            distanceInfo = "距德胜门地铁站步行200米",
            promotions = listOf(
                PromotionItem("store_first", "门店首单", "优惠38", "first_order", "#FF6B6B"),
                PromotionItem("weekend", "周末特惠", "8.5折起", "weekend", "#4A90E2"),
                PromotionItem("early_bird", "早鸟特价", "限时抢购", "early", "#32C832")
            ),
            roomTypes = listOf(
                RoomType(
                    id = "room_bj_002_1",
                    name = "商务标准间",
                    imageUrl = "room_bj_002_1.jpg",
                    description = "现代商务风格+办公设施+高速网络",
                    features = listOf("1张1.8米大床", "20-25㎡", "2人入住", "5-15层"),
                    bedInfo = "1张1.8米大床",
                    area = "20-25㎡",
                    maxGuests = 2,
                    floor = "5-15层",
                    breakfast = "含早餐",
                    cancellationPolicy = "入住当天15:00前可免费取消",
                    price = 151,
                    originalPrice = 189,
                    badge = "什刹海网最经济型酒店热卖 No.2"
                ),
                RoomType(
                    id = "room_bj_002_2",
                    name = "影音主题房",
                    imageUrl = "room_bj_002_2.jpg",
                    description = "专业影音设备+隔音效果+娱乐体验",
                    features = listOf("1张2米大床", "28-32㎡", "2人入住", "6-12层"),
                    bedInfo = "1张2米大床",
                    area = "28-32㎡",
                    maxGuests = 2,
                    floor = "6-12层",
                    breakfast = "含早餐",
                    cancellationPolicy = "入住前1天可免费取消",
                    price = 198,
                    originalPrice = 268
                ),
                RoomType(
                    id = "room_bj_002_3",
                    name = "行政套房",
                    imageUrl = "room_bj_002_3.jpg",
                    description = "豪华装修+独立客厅+VIP服务",
                    features = listOf("1张2.2米大床", "45-50㎡", "3人入住", "15-20层"),
                    bedInfo = "1张2.2米大床",
                    area = "45-50㎡",
                    maxGuests = 3,
                    floor = "15-20层",
                    breakfast = "含双人早餐",
                    cancellationPolicy = "入住前2天可免费取消",
                    price = 288,
                    originalPrice = 380
                )
            )
        ),
        
        // 为所有酒店创建通用详情数据
        "hotel_bj_003" to createGenericHotelDetail("hotel_bj_003"),
        "hotel_bj_004" to createGenericHotelDetail("hotel_bj_004"),
        "hotel_bj_005" to createGenericHotelDetail("hotel_bj_005"),
        "hotel_sh_001" to createGenericHotelDetail("hotel_sh_001"),
        "hotel_sh_002" to createGenericHotelDetail("hotel_sh_002"),
        "hotel_sh_003" to createGenericHotelDetail("hotel_sh_003"),
        "hotel_sh_004" to createGenericHotelDetail("hotel_sh_004"),
        "hotel_sh_005" to createGenericHotelDetail("hotel_sh_005"),
        "hotel_gz_001" to createGenericHotelDetail("hotel_gz_001"),
        "hotel_gz_002" to createGenericHotelDetail("hotel_gz_002"),
        "hotel_gz_003" to createGenericHotelDetail("hotel_gz_003"),
        "hotel_gz_004" to createGenericHotelDetail("hotel_gz_004"),
        "hotel_gz_005" to createGenericHotelDetail("hotel_gz_005"),
        "hotel_sz_001" to createGenericHotelDetail("hotel_sz_001"),
        "hotel_sz_002" to createGenericHotelDetail("hotel_sz_002"),
        "hotel_sz_003" to createGenericHotelDetail("hotel_sz_003"),
        "hotel_sz_004" to createGenericHotelDetail("hotel_sz_004"),
        "hotel_sz_005" to createGenericHotelDetail("hotel_sz_005"),
        "hotel_hz_001" to createGenericHotelDetail("hotel_hz_001"),
        "hotel_hz_002" to createGenericHotelDetail("hotel_hz_002"),
        "hotel_hz_003" to createGenericHotelDetail("hotel_hz_003"),
        "hotel_hz_004" to createGenericHotelDetail("hotel_hz_004"),
        "hotel_hz_005" to createGenericHotelDetail("hotel_hz_005")
    )
    
    private fun createGenericHotelDetail(hotelId: String): HotelDetailData {
        // 从现有酒店列表中获取基本信息
        val hotelListModel = HotelListTabModelImpl(context)
        val searchParams = HotelListSearchParams(
            city = "北京", // 默认城市，会在getHotelDetailData中更新
            checkInDate = DateUtils.getCurrentDate(),
            checkOutDate = DateUtils.getTomorrowDate(),
            roomCount = 1,
            guestCount = 1,
            adultCount = 1,
            childCount = 0
        )
        
        // 获取所有酒店数据
        val allCities = listOf("北京", "上海", "广州", "深圳", "杭州")
        var hotel: HotelItem? = null
        
        for (city in allCities) {
            val cityParams = searchParams.copy(city = city)
            val hotels = hotelListModel.searchHotels(cityParams)
            hotel = hotels.find { it.id == hotelId }
            if (hotel != null) break
        }
        
        hotel = hotel ?: HotelItem(
            id = hotelId,
            name = "酒店详情",
            imageUrl = "$hotelId.jpg",
            rating = 4.5,
            reviewCount = 100,
            favoriteCount = 500,
            location = "市中心",
            description = "舒适便利的住宿选择",
            amenities = listOf("免费WiFi", "停车位"),
            price = 200,
            city = "北京",
            availableDates = listOf(DateUtils.getCurrentDate(), DateUtils.getTomorrowDate())
        )
        
        return HotelDetailData(
            hotel = hotel,
            searchParams = searchParams,
            imageGallery = listOf(
                HotelImage("img_1", "${hotelId}_cover.jpg", "cover", "封面"),
                HotelImage("img_2", "${hotelId}_featured.jpg", "featured", "精选"),
                HotelImage("img_3", "${hotelId}_location.jpg", "location", "位置"),
                HotelImage("img_4", "${hotelId}_album_1.jpg", "album", "相册1")
            ),
            hotelInfo = HotelDetailInfo(
                name = hotel.name,
                badges = listOf(
                    HotelBadge("vip", "优享会", "vip"),
                    HotelBadge("year", "2022年开业", "year")
                )
            ),
            amenities = listOf(
                HotelAmenity("wifi", "📶", "免费WiFi"),
                HotelAmenity("parking", "🅿️", "免费停车"),
                HotelAmenity("service", "🛎️", "24小时服务"),
                HotelAmenity("clean", "🧹", "清洁服务"),
                HotelAmenity("policy", "📋", "设施政策")
            ),
            reviewSummary = ReviewSummary(
                rating = hotel.rating,
                reviewCount = hotel.reviewCount,
                summary = hotel.description
            ),
            locationInfo = LocationInfo(
                address = hotel.location,
                distanceInfo = "交通便利"
            ),
            promotions = listOf(
                PromotionItem("first_stay", "首住特惠", "85折起", "first_stay", "#FF6B6B"),
                PromotionItem("weekend", "周末特惠", "9折", "weekend", "#4A90E2"),
                PromotionItem("coupon", "领券", "", "coupon", "#FF9500")
            ),
            roomTypes = listOf(
                RoomType(
                    id = "${hotelId}_room_1",
                    name = "标准大床房",
                    imageUrl = "${hotelId}_room_1.jpg",
                    description = "舒适温馨+现代设施",
                    features = listOf("1张1.8米大床", "20-25㎡", "2人入住", "中层"),
                    bedInfo = "1张1.8米大床",
                    area = "20-25㎡",
                    maxGuests = 2,
                    floor = "中层",
                    breakfast = "无早餐",
                    cancellationPolicy = "入住当天18:00前可免费取消",
                    price = hotel.price,
                    originalPrice = hotel.price + 50
                ),
                RoomType(
                    id = "${hotelId}_room_2",
                    name = "豪华双床房",
                    imageUrl = "${hotelId}_room_2.jpg",
                    description = "宽敞舒适+双床配置",
                    features = listOf("2张1.5米床", "25-30㎡", "4人入住", "高层"),
                    bedInfo = "2张1.5米床",
                    area = "25-30㎡",
                    maxGuests = 4,
                    floor = "高层",
                    breakfast = "含早餐",
                    cancellationPolicy = "入住前1天可免费取消",
                    price = hotel.price + 80,
                    originalPrice = hotel.price + 150
                ),
                RoomType(
                    id = "${hotelId}_room_3",
                    name = "商务套房",
                    imageUrl = "${hotelId}_room_3.jpg",
                    description = "商务办公+独立客厅",
                    features = listOf("1张2米大床", "35-40㎡", "3人入住", "顶层"),
                    bedInfo = "1张2米大床",
                    area = "35-40㎡",
                    maxGuests = 3,
                    floor = "顶层",
                    breakfast = "含双人早餐",
                    cancellationPolicy = "入住前2天可免费取消",
                    price = hotel.price + 150,
                    originalPrice = hotel.price + 250
                )
            ),
            filterTags = listOf(
                RoomFilterTag("double_bed", "双床房"),
                RoomFilterTag("pickup_service", "送机套餐"),
                RoomFilterTag("family_room", "家庭房"),
                RoomFilterTag("private_bath", "私人卫生间"),
                RoomFilterTag("return", "返"),
                RoomFilterTag("filter", "筛选")
            )
        )
    }
    
    private fun createHotelDetail(
        hotelId: String,
        name: String,
        badges: List<HotelBadge>,
        amenities: List<HotelAmenity>,
        rating: Double,
        reviewCount: Int,
        summary: String,
        address: String,
        distanceInfo: String,
        promotions: List<PromotionItem>,
        roomTypes: List<RoomType>
    ): HotelDetailData {
        // 从现有酒店列表中获取基本信息
        val hotelListModel = HotelListTabModelImpl(context)
        val searchParams = HotelListSearchParams(
            city = "北京",
            checkInDate = DateUtils.getCurrentDate(),
            checkOutDate = DateUtils.getTomorrowDate(),
            roomCount = 1,
            guestCount = 1,
            adultCount = 1,
            childCount = 0
        )
        val hotels = hotelListModel.searchHotels(searchParams)
        val hotel = hotels.find { it.id == hotelId } ?: hotels.first()
        
        return HotelDetailData(
            hotel = hotel,
            searchParams = searchParams,
            imageGallery = listOf(
                HotelImage("img_1", "${hotelId}_cover.jpg", "cover", "封面"),
                HotelImage("img_2", "${hotelId}_featured.jpg", "featured", "精选"),
                HotelImage("img_3", "${hotelId}_location.jpg", "location", "位置"),
                HotelImage("img_4", "${hotelId}_album_1.jpg", "album", "相册1"),
                HotelImage("img_5", "${hotelId}_album_2.jpg", "album", "相册2")
            ),
            hotelInfo = HotelDetailInfo(
                name = name,
                badges = badges
            ),
            amenities = amenities,
            reviewSummary = ReviewSummary(
                rating = rating,
                reviewCount = reviewCount,
                summary = summary
            ),
            locationInfo = LocationInfo(
                address = address,
                distanceInfo = distanceInfo
            ),
            promotions = promotions,
            roomTypes = roomTypes,
            filterTags = listOf(
                RoomFilterTag("double_bed", "双床房"),
                RoomFilterTag("pickup_service", "送机套餐"),
                RoomFilterTag("family_room", "家庭房"),
                RoomFilterTag("private_bath", "私人卫生间"),
                RoomFilterTag("return", "返"),
                RoomFilterTag("filter", "筛选")
            )
        )
    }
    
    override fun getHotelDetailData(hotelId: String, searchParams: HotelListSearchParams): HotelDetailData? {
        return hotelDetailsMap[hotelId]?.copy(searchParams = searchParams)
    }
    
    override fun toggleFavorite(hotelId: String): Boolean {
        val key = "favorite_$hotelId"
        val isFavorite = sharedPreferences.getBoolean(key, false)
        val newStatus = !isFavorite
        
        sharedPreferences.edit()
            .putBoolean(key, newStatus)
            .apply()
            
        saveHotelDetailAction("toggle_favorite", mapOf(
            "hotel_id" to hotelId,
            "is_favorite" to newStatus
        ))
        
        return newStatus
    }
    
    override fun addToCart(hotelId: String, roomTypeId: String) {
        saveHotelDetailAction("add_to_cart", mapOf(
            "hotel_id" to hotelId,
            "room_type_id" to roomTypeId,
            "timestamp" to System.currentTimeMillis()
        ))
    }
    
    override fun getRoomTypes(hotelId: String): List<RoomType> {
        return hotelDetailsMap[hotelId]?.roomTypes ?: emptyList()
    }
    
    override fun filterRoomTypes(roomTypes: List<RoomType>, tags: List<RoomFilterTag>): List<RoomType> {
        val selectedTags = tags.filter { it.isSelected }
        if (selectedTags.isEmpty()) return roomTypes
        
        return roomTypes.filter { roomType ->
            selectedTags.all { tag ->
                when (tag.id) {
                    "double_bed" -> roomType.bedInfo.contains("双床") || roomType.bedInfo.contains("2张")
                    "pickup_service" -> roomType.description.contains("接送") || roomType.features.any { it.contains("接送") }
                    "family_room" -> roomType.name.contains("家庭") || roomType.maxGuests >= 4
                    "private_bath" -> true // 假设所有房间都有私人卫生间
                    else -> true
                }
            }
        }
    }
    
    override fun saveHotelDetailAction(action: String, data: Map<String, Any>) {
        val actionData = mapOf(
            "action" to action,
            "timestamp" to System.currentTimeMillis(),
            "data" to data
        )
        
        val actionJson = gson.toJson(actionData)
        sharedPreferences.edit()
            .putString("hotel_detail_action_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
}