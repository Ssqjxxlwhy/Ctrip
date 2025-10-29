package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.example.Ctrip.model.City
import com.example.Ctrip.model.LocationRegion

class FlightDestinationSelectTabModel(private val context: Context) : FlightDestinationSelectTabContract.Model {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("destination_select_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    override fun getDestinationSelectionData(): DestinationSelectionData? {
        return try {
            DestinationSelectionData(
                history = getHistoryDestinations(),
                specialOffers = getSpecialOffers(),
                hotCities = getHotCities(),
                reputationRankings = getReputationRankings(),
                hotRegions = getHotRegions(),
                isLocationEnabled = false
            )
        } catch (e: Exception) {
            null
        }
    }
    
    private fun getHistoryDestinations(): List<City> {
        // 返回空列表，表示没有历史记录
        return emptyList()
    }
    
    private fun getSpecialOffers(): List<DestinationSpecialOffer> {
        return listOf(
            DestinationSpecialOffer(
                id = "low_price",
                title = "搜全国低价",
                subtitle = "",
                type = "low_price",
                backgroundColor = "#4CAF50"
            ),
            DestinationSpecialOffer(
                id = "morocco",
                title = "摩洛哥",
                subtitle = "免签/落地签",
                type = "visa_free",
                backgroundColor = "#2196F3"
            )
        )
    }
    
    private fun getHotCities(): List<City> {
        return listOf(
            City("SH", "上海", "SHA", "上海", "中国", true, "Shanghai", "S"),
            City("BJ", "北京", "PEK", "北京", "中国", true, "Beijing", "B"),
            City("CD", "成都", "CTU", "四川", "中国", true, "Chengdu", "C"),
            City("GZ", "广州", "CAN", "广东", "中国", true, "Guangzhou", "G"),
            City("SZ", "深圳", "SZX", "广东", "中国", true, "Shenzhen", "S"),
            City("CQ", "重庆", "CKG", "重庆", "中国", true, "Chongqing", "C"),
            City("HZ", "杭州", "HGH", "浙江", "中国", true, "Hangzhou", "H"),
            City("XA", "西安", "XIY", "陕西", "中国", true, "Xian", "X")
        )
    }
    
    private fun getReputationRankings(): List<ReputationRanking> {
        return listOf(
            ReputationRanking(
                id = "must_visit",
                title = "必打卡榜",
                icon = "📸",
                backgroundColor = "#FF9800",
                cities = listOf(
                    RankingCity(1, City("LJ", "丽江", "LJG", "云南", "中国", false, "Lijiang", "L")),
                    RankingCity(2, City("BJ", "北京", "PEK", "北京", "中国", false, "Beijing", "B")),
                    RankingCity(3, City("SH", "上海", "SHA", "上海", "中国", false, "Shanghai", "S"))
                )
            ),
            ReputationRanking(
                id = "family",
                title = "亲子榜",
                icon = "👨‍👩‍👧‍👦",
                backgroundColor = "#E91E63",
                cities = listOf(
                    RankingCity(1, City("BJ", "北京", "PEK", "北京", "中国", false, "Beijing", "B")),
                    RankingCity(2, City("SH", "上海", "SHA", "上海", "中国", false, "Shanghai", "S")),
                    RankingCity(3, City("DL", "大连", "DLC", "辽宁", "中国", false, "Dalian", "D"))
                )
            ),
            ReputationRanking(
                id = "autumn",
                title = "赏秋榜",
                icon = "🍂",
                backgroundColor = "#FF5722",
                cities = listOf(
                    RankingCity(1, City("BJ", "北京", "PEK", "北京", "中国", false, "Beijing", "B")),
                    RankingCity(2, City("SH", "上海", "SHA", "上海", "中国", false, "Shanghai", "S")),
                    RankingCity(3, City("HZ", "湖州", "HUZ", "浙江", "中国", false, "Huzhou", "H"))
                )
            )
        )
    }
    
    private fun getHotRegions(): List<HotRegion> {
        return listOf(
            HotRegion(
                id = "jiangzhehu",
                name = "江浙沪",
                imageUrl = "",
                cities = listOf(
                    City("SH", "上海", "SHA", "上海", "中国", false, "Shanghai", "S"),
                    City("HZ", "杭州", "HGH", "浙江", "中国", false, "Hangzhou", "H"),
                    City("NJ", "南京", "NKG", "江苏", "中国", false, "Nanjing", "N")
                )
            ),
            HotRegion(
                id = "heijiliao",
                name = "黑吉辽",
                imageUrl = "",
                cities = listOf(
                    City("HRB", "哈尔滨", "HRB", "黑龙江", "中国", false, "Harbin", "H"),
                    City("CC", "长春", "CGQ", "吉林", "中国", false, "Changchun", "C"),
                    City("SY", "沈阳", "SHE", "辽宁", "中国", false, "Shenyang", "S")
                )
            ),
            HotRegion(
                id = "jingjinjii",
                name = "京津冀",
                imageUrl = "",
                cities = listOf(
                    City("BJ", "北京", "PEK", "北京", "中国", false, "Beijing", "B"),
                    City("TJ", "天津", "TSN", "天津", "中国", false, "Tianjin", "T"),
                    City("SJZ", "石家庄", "SJW", "河北", "中国", false, "Shijiazhuang", "S")
                )
            )
        )
    }
    
    override fun searchDestinations(query: String): List<City> {
        val allCities = getHotCities()
        return if (query.isBlank()) {
            allCities
        } else {
            allCities.filter { 
                it.cityName.contains(query, ignoreCase = true) || 
                it.pinyin.contains(query, ignoreCase = true)
            }
        }
    }
    
    override fun getDestinationsByRegion(region: LocationRegion): List<City> {
        return when (region) {
            LocationRegion.DOMESTIC -> getHotCities()
            LocationRegion.INTERNATIONAL -> listOf(
                City("TYO", "东京", "NRT", "东京", "日本", false, "Tokyo", "T"),
                City("SEL", "首尔", "ICN", "首尔", "韩国", false, "Seoul", "S"),
                City("BKK", "曼谷", "BKK", "曼谷", "泰国", false, "Bangkok", "B"),
                City("SIN", "新加坡", "SIN", "新加坡", "新加坡", false, "Singapore", "S")
            )
        }
    }
    
    override fun getDestinationsByCategory(category: String): List<City> {
        return when (category) {
            "历史" -> getHistoryDestinations()
            "热门" -> getHotCities()
            "主题" -> getReputationRankings().flatMap { ranking -> 
                ranking.cities.map { it.city }
            }
            "地区" -> getHotRegions().flatMap { it.cities }
            else -> getHotCities()
        }
    }
    
    override fun saveDestinationSelection(city: City) {
        val selectionData = mapOf(
            "action" to "destination_selected",
            "timestamp" to System.currentTimeMillis(),
            "city_id" to city.cityId,
            "city_name" to city.cityName,
            "city_code" to city.cityCode
        )
        
        val actionJson = gson.toJson(selectionData)
        sharedPreferences.edit()
            .putString("last_destination_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun updateSearchHistory(city: City) {
        val currentHistory = getHistoryDestinations().toMutableList()
        
        // Remove if already exists
        currentHistory.removeAll { it.cityId == city.cityId }
        
        // Add to front
        currentHistory.add(0, city)
        
        // Keep only last 10
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        // Save to preferences
        val historyJson = gson.toJson(currentHistory)
        sharedPreferences.edit()
            .putString("destination_search_history", historyJson)
            .apply()
    }
}