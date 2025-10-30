package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.LocalDate

interface HotelListTabModel : HotelListTabContract.Model

class HotelListTabModelImpl(private val context: Context) : HotelListTabModel {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("hotel_list_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // 预设的酒店数据 - 5个城市，10月20日-25日可预订
    private val hotelData = listOf(
        // 北京酒店 (5个)
        HotelItem(
            id = "hotel_bj_001",
            name = "北京一家人民宿(大兴国际机场店)",
            imageUrl = "hotel_bj_001.jpg",
            rating = 4.7,
            reviewCount = 250,
            favoriteCount = 1337,
            location = "大兴机场·近于大妮便民超市",
            description = "离大兴机场很近有接送机服务很方便",
            amenities = listOf("免费接送机", "免费停车", "无线接置", "家庭房"),
            price = 104,
            originalPrice = 185,
            discountInfo = "热卖！低价房仅剩3间",
            benefitInfo = "首住特惠 4晚优惠81",
            promotionCount = 4,
            city = "北京",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_bj_002",
            name = "北京兰岩酒店(德胜门鼓楼大街地铁站店)",
            imageUrl = "hotel_bj_002.jpg",
            rating = 4.5,
            reviewCount = 2995,
            favoriteCount = 26000,
            location = "近德胜门·南锣鼓巷",
            description = "地理位置好，住宿环境干净卫生",
            amenities = listOf("自营影音房", "洗衣房", "管家服务", "无线接置"),
            price = 151,
            originalPrice = 189,
            discountInfo = "什刹海网最经济型酒店热卖 No.2",
            benefitInfo = "门店首单 优惠38",
            promotionCount = 10,
            city = "北京",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_bj_003",
            name = "北京旭阳趣舍(北京大兴国际机场店)",
            imageUrl = "hotel_bj_003.jpg",
            rating = 4.8,
            reviewCount = 640,
            favoriteCount = 3507,
            location = "近徐庄生鲜超市·银杏观光园",
            description = "服务周到热情，中转方便，居住舒适",
            amenities = listOf("延时退房", "四合院", "免费停车", "管家服务"),
            price = 65,
            originalPrice = 103,
            discountInfo = "热卖！低价房仅剩5间",
            benefitInfo = "首住特惠 3晚优惠38",
            promotionCount = 3,
            city = "北京",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_bj_004",
            name = "北京王府井希尔顿酒店",
            imageUrl = "hotel_bj_004.jpg",
            rating = 4.6,
            reviewCount = 1856,
            favoriteCount = 4200,
            location = "王府井·天安门广场",
            description = "位于王府井商业街，近地铁站",
            amenities = listOf("地铁直达", "商务中心", "健身房", "餐厅"),
            price = 380,
            originalPrice = 480,
            discountInfo = "商务特惠",
            benefitInfo = "首住特惠 2晚优惠150",
            promotionCount = 6,
            city = "北京",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_bj_005",
            name = "北京颐和园如家民宿",
            imageUrl = "hotel_bj_005.jpg",
            rating = 4.3,
            reviewCount = 580,
            favoriteCount = 1200,
            location = "颐和园·圆明园",
            description = "古典园林风格，环境优雅",
            amenities = listOf("古典装修", "免费WiFi", "停车位", "茶室"),
            price = 180,
            originalPrice = 220,
            discountInfo = "园林特惠",
            benefitInfo = "民宿特价 3晚优惠80",
            promotionCount = 2,
            city = "北京",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        
        // 上海酒店 (5个)
        HotelItem(
            id = "hotel_sh_001",
            name = "上海外滩茂悦大酒店",
            imageUrl = "hotel_sh_001.jpg",
            rating = 4.6,
            reviewCount = 1856,
            favoriteCount = 4521,
            location = "外滩·南京东路",
            description = "黄浦江畔，外滩风景尽收眼底",
            amenities = listOf("江景房", "健身房", "商务中心", "接送服务"),
            price = 298,
            originalPrice = 398,
            discountInfo = "江景特惠，仅剩2间",
            benefitInfo = "首住特惠 2晚优惠120",
            promotionCount = 5,
            city = "上海",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sh_002",
            name = "上海迪士尼乐园酒店",
            imageUrl = "hotel_sh_002.jpg",
            rating = 4.9,
            reviewCount = 3200,
            favoriteCount = 8900,
            location = "迪士尼度假区",
            description = "迪士尼主题酒店，孩子的梦想天堂",
            amenities = listOf("主题房间", "儿童乐园", "免费班车", "角色见面"),
            price = 888,
            originalPrice = 1200,
            discountInfo = "亲子套餐特惠",
            benefitInfo = "家庭套餐 3晚优惠500",
            promotionCount = 8,
            city = "上海",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sh_003",
            name = "上海虹桥机场华美达酒店",
            imageUrl = "hotel_sh_003.jpg",
            rating = 4.4,
            reviewCount = 980,
            favoriteCount = 2100,
            location = "虹桥机场·近地铁",
            description = "机场酒店，交通便利",
            amenities = listOf("免费接送", "地铁直达", "商务中心", "24小时前台"),
            price = 220,
            originalPrice = 280,
            discountInfo = "机场特惠",
            benefitInfo = "商旅优惠 1晚立减50",
            promotionCount = 3,
            city = "上海",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sh_004",
            name = "上海南京路步行街亚朵酒店",
            imageUrl = "hotel_sh_004.jpg",
            rating = 4.7,
            reviewCount = 1500,
            favoriteCount = 3200,
            location = "南京路步行街·人民广场",
            description = "繁华商圈中心，购物便利",
            amenities = listOf("地铁直达", "购物中心", "餐厅", "健身房"),
            price = 350,
            originalPrice = 420,
            discountInfo = "购物特惠",
            benefitInfo = "商圈优惠 2晚优惠100",
            promotionCount = 4,
            city = "上海",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sh_005",
            name = "上海田子坊创意民宿",
            imageUrl = "hotel_sh_005.jpg",
            rating = 4.5,
            reviewCount = 680,
            favoriteCount = 1800,
            location = "田子坊·新天地",
            description = "文艺创意空间，独特体验",
            amenities = listOf("艺术装修", "咖啡厅", "创意空间", "免费WiFi"),
            price = 180,
            originalPrice = 230,
            discountInfo = "文艺特惠",
            benefitInfo = "民宿特价 3晚优惠70",
            promotionCount = 2,
            city = "上海",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        
        // 广州酒店 (5个)
        HotelItem(
            id = "hotel_gz_001",
            name = "广州白云国际机场希尔顿酒店",
            imageUrl = "hotel_gz_001.jpg",
            rating = 4.7,
            reviewCount = 980,
            favoriteCount = 2100,
            location = "白云国际机场",
            description = "机场内酒店，转机最佳选择",
            amenities = listOf("机场内", "免费WiFi", "健身房", "商务中心"),
            price = 456,
            originalPrice = 580,
            discountInfo = "转机特惠",
            benefitInfo = "商旅优惠 1晚立减80",
            promotionCount = 2,
            city = "广州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_gz_002",
            name = "广州珠江新城丽思卡尔顿酒店",
            imageUrl = "hotel_gz_002.jpg",
            rating = 4.8,
            reviewCount = 1500,
            favoriteCount = 3800,
            location = "珠江新城·天河商圈",
            description = "奢华五星级酒店，俯瞰珠江美景",
            amenities = listOf("江景房", "行政酒廊", "游泳池", "SPA"),
            price = 680,
            originalPrice = 850,
            discountInfo = "周末特惠",
            benefitInfo = "豪华套房 2晚优惠300",
            promotionCount = 6,
            city = "广州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_gz_003",
            name = "广州长隆酒店",
            imageUrl = "hotel_gz_003.jpg",
            rating = 4.6,
            reviewCount = 2200,
            favoriteCount = 5100,
            location = "长隆旅游度假区",
            description = "亲子度假首选，主题乐园酒店",
            amenities = listOf("主题房间", "动物园", "游乐场", "儿童乐园"),
            price = 580,
            originalPrice = 720,
            discountInfo = "亲子特惠",
            benefitInfo = "家庭套餐 3晚优惠200",
            promotionCount = 7,
            city = "广州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_gz_004",
            name = "广州北京路亚朵酒店",
            imageUrl = "hotel_gz_004.jpg",
            rating = 4.4,
            reviewCount = 890,
            favoriteCount = 1800,
            location = "北京路步行街·越秀公园",
            description = "商业中心地带，购物便利",
            amenities = listOf("地铁直达", "购物中心", "餐厅", "免费WiFi"),
            price = 280,
            originalPrice = 350,
            discountInfo = "购物特惠",
            benefitInfo = "商圈优惠 2晚优惠80",
            promotionCount = 3,
            city = "广州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_gz_005",
            name = "广州沙面岛精品民宿",
            imageUrl = "hotel_gz_005.jpg",
            rating = 4.5,
            reviewCount = 520,
            favoriteCount = 1200,
            location = "沙面岛·珠江边",
            description = "历史建筑改造，欧式风情",
            amenities = listOf("历史建筑", "江景阳台", "咖啡厅", "免费WiFi"),
            price = 220,
            originalPrice = 280,
            discountInfo = "历史特惠",
            benefitInfo = "民宿特价 3晚优惠90",
            promotionCount = 2,
            city = "广州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        
        // 深圳酒店 (5个)
        HotelItem(
            id = "hotel_sz_001",
            name = "深圳福田香格里拉大酒店",
            imageUrl = "hotel_sz_001.jpg",
            rating = 4.6,
            reviewCount = 1200,
            favoriteCount = 2800,
            location = "福田中心区·会展中心",
            description = "商务出行首选，地铁直达",
            amenities = listOf("商务中心", "会议室", "健身房", "地铁直达"),
            price = 520,
            originalPrice = 650,
            discountInfo = "商务优惠",
            benefitInfo = "会议套餐 3晚优惠200",
            promotionCount = 4,
            city = "深圳",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sz_002",
            name = "深圳南山海上世界万豪酒店",
            imageUrl = "hotel_sz_002.jpg",
            rating = 4.7,
            reviewCount = 1580,
            favoriteCount = 3600,
            location = "南山·海上世界",
            description = "海景酒店，现代化设施",
            amenities = listOf("海景房", "游泳池", "健身房", "SPA"),
            price = 450,
            originalPrice = 560,
            discountInfo = "海景特惠",
            benefitInfo = "海景套房 2晚优惠150",
            promotionCount = 5,
            city = "深圳",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sz_003",
            name = "深圳罗湖口岸如家酒店",
            imageUrl = "hotel_sz_003.jpg",
            rating = 4.3,
            reviewCount = 890,
            favoriteCount = 1500,
            location = "罗湖口岸·东门商圈",
            description = "口岸便利，购物方便",
            amenities = listOf("口岸直达", "购物中心", "免费WiFi", "24小时前台"),
            price = 180,
            originalPrice = 230,
            discountInfo = "口岸特惠",
            benefitInfo = "商旅优惠 1晚立减40",
            promotionCount = 2,
            city = "深圳",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sz_004",
            name = "深圳宝安机场希尔顿酒店",
            imageUrl = "hotel_sz_004.jpg",
            rating = 4.5,
            reviewCount = 1100,
            favoriteCount = 2200,
            location = "宝安国际机场",
            description = "机场酒店，转机便利",
            amenities = listOf("免费接送", "商务中心", "健身房", "餐厅"),
            price = 380,
            originalPrice = 480,
            discountInfo = "机场特惠",
            benefitInfo = "转机优惠 1晚立减70",
            promotionCount = 3,
            city = "深圳",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_sz_005",
            name = "深圳华强北商圈民宿",
            imageUrl = "hotel_sz_005.jpg",
            rating = 4.2,
            reviewCount = 620,
            favoriteCount = 1100,
            location = "华强北·电子市场",
            description = "科技商圈中心，购物便利",
            amenities = listOf("科技装修", "购物便利", "免费WiFi", "地铁直达"),
            price = 150,
            originalPrice = 200,
            discountInfo = "科技特惠",
            benefitInfo = "民宿特价 3晚优惠60",
            promotionCount = 1,
            city = "深圳",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        
        // 杭州酒店 (5个)
        HotelItem(
            id = "hotel_hz_001",
            name = "杭州西湖国宾馆",
            imageUrl = "hotel_hz_001.jpg",
            rating = 4.9,
            reviewCount = 800,
            favoriteCount = 1900,
            location = "西湖风景区·断桥残雪",
            description = "西湖畔历史名馆，古典园林式酒店",
            amenities = listOf("西湖景观", "古典园林", "茶室", "精品餐厅"),
            price = 780,
            originalPrice = 980,
            discountInfo = "湖景特惠",
            benefitInfo = "园景套房 2晚优惠250",
            promotionCount = 3,
            city = "杭州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_hz_002",
            name = "杭州萧山国际机场希尔顿酒店",
            imageUrl = "hotel_hz_002.jpg",
            rating = 4.5,
            reviewCount = 650,
            favoriteCount = 1200,
            location = "萧山国际机场",
            description = "机场酒店，便捷出行",
            amenities = listOf("免费接送", "24小时前台", "商务中心", "健身房"),
            price = 320,
            originalPrice = 420,
            discountInfo = "机场特惠",
            benefitInfo = "转机优惠 1晚立减60",
            promotionCount = 2,
            city = "杭州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_hz_003",
            name = "杭州河坊街亚朵酒店",
            imageUrl = "hotel_hz_003.jpg",
            rating = 4.6,
            reviewCount = 980,
            favoriteCount = 2100,
            location = "河坊街·南宋御街",
            description = "古街新韵，文化体验",
            amenities = listOf("古街位置", "文化体验", "茶室", "地铁直达"),
            price = 280,
            originalPrice = 350,
            discountInfo = "文化特惠",
            benefitInfo = "古街优惠 2晚优惠80",
            promotionCount = 4,
            city = "杭州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_hz_004",
            name = "杭州钱塘江景万豪酒店",
            imageUrl = "hotel_hz_004.jpg",
            rating = 4.7,
            reviewCount = 1200,
            favoriteCount = 2800,
            location = "钱塘江边·滨江区",
            description = "江景房间，现代化设施",
            amenities = listOf("江景房", "健身房", "游泳池", "商务中心"),
            price = 420,
            originalPrice = 520,
            discountInfo = "江景特惠",
            benefitInfo = "江景套房 2晚优惠120",
            promotionCount = 5,
            city = "杭州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        ),
        HotelItem(
            id = "hotel_hz_005",
            name = "杭州西溪湿地民宿",
            imageUrl = "hotel_hz_005.jpg",
            rating = 4.4,
            reviewCount = 580,
            favoriteCount = 1300,
            location = "西溪湿地·自然保护区",
            description = "生态民宿，亲近自然",
            amenities = listOf("生态环境", "自然景观", "茶室", "免费WiFi"),
            price = 200,
            originalPrice = 260,
            discountInfo = "生态特惠",
            benefitInfo = "民宿特价 3晚优惠80",
            promotionCount = 2,
            city = "杭州",
            availableDates = generateDateRange(LocalDate.of(2025, 10, 20), LocalDate.of(2025, 10, 25))
        )
    )
    
    private fun generateDateRange(start: LocalDate, end: LocalDate): List<LocalDate> {
        val dates = mutableListOf<LocalDate>()
        var current = start
        while (!current.isAfter(end)) {
            dates.add(current)
            current = current.plusDays(1)
        }
        return dates
    }
    
    override fun getHotelListData(searchParams: HotelListSearchParams): HotelListData? {
        val availableHotels = searchHotels(searchParams)
        
        return HotelListData(
            searchParams = searchParams,
            sortOptions = getSortOptions(),
            filterTags = getFilterTags(),
            firstStayBenefit = getFirstStayBenefit(),
            hotels = availableHotels
        )
    }
    
    override fun searchHotels(searchParams: HotelListSearchParams): List<HotelItem> {
        return hotelData.filter { hotel ->
            // 筛选城市
            hotel.city == searchParams.city &&
            // 筛选日期范围
            searchParams.checkInDate in hotel.availableDates &&
            searchParams.checkOutDate in hotel.availableDates &&
            // 搜索关键词筛选
            (searchParams.searchQuery.isEmpty() || 
             hotel.name.contains(searchParams.searchQuery, ignoreCase = true) ||
             hotel.location.contains(searchParams.searchQuery, ignoreCase = true))
        }
    }
    
    override fun sortHotels(hotels: List<HotelItem>, sortId: String): List<HotelItem> {
        return when (sortId) {
            "welcome" -> hotels.sortedByDescending { it.reviewCount }
            "distance" -> hotels.sortedBy { it.location }
            "price_low" -> hotels.sortedBy { it.price }
            "price_high" -> hotels.sortedByDescending { it.price }
            "rating" -> hotels.sortedByDescending { it.rating }
            else -> hotels
        }
    }
    
    override fun filterHotels(hotels: List<HotelItem>, tags: List<FilterTag>): List<HotelItem> {
        val selectedTags = tags.filter { it.isSelected }
        if (selectedTags.isEmpty()) return hotels
        
        return hotels.filter { hotel ->
            selectedTags.all { tag ->
                when (tag.id) {
                    "tiananmen" -> hotel.location.contains("天安门") || hotel.name.contains("天安门")
                    "metro" -> hotel.amenities.any { it.contains("地铁") } || hotel.location.contains("地铁") || hotel.amenities.any { it.contains("直达") }
                    "homestay" -> hotel.name.contains("民宿")
                    "points_10x" -> hotel.benefitInfo?.contains("返10倍积分") == true
                    "rating_45" -> hotel.rating >= 4.5
                    else -> true
                }
            }
        }
    }
    
    override fun saveHotelSelection(hotel: HotelItem) {
        val selectionData = mapOf(
            "action" to "hotel_selected",
            "timestamp" to System.currentTimeMillis(),
            "hotel_id" to hotel.id,
            "hotel_name" to hotel.name,
            "city" to hotel.city,
            "price" to hotel.price
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_hotel_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    private fun getSortOptions(): List<HotelSortOption> {
        return listOf(
            HotelSortOption("welcome", "欢迎度排序", true),
            HotelSortOption("distance", "位置距离", false),
            HotelSortOption("filter", "筛选", false, false)
        )
    }
    
    private fun getFilterTags(): List<FilterTag> {
        return listOf(
            FilterTag("tiananmen", "天安门广场"),
            FilterTag("metro", "近地铁"),
            FilterTag("homestay", "民宿"),
            FilterTag("points_10x", "返10倍积分"),
            FilterTag("rating_45", "4.5分以上")
        )
    }
    
    private fun getFirstStayBenefit(): FirstStayBenefit {
        return FirstStayBenefit(
            title = "首住好礼",
            description = "首住特惠85折起",
            discount = "85折起"
        )
    }
}