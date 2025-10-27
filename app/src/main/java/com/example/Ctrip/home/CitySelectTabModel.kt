package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

interface CitySelectTabModel : CitySelectTabContract.Model

class CitySelectTabModelImpl(private val context: Context) : CitySelectTabModel {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("city_select_data", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    // 模拟城市数据
    private val allCities = listOf(
        // 热门城市
        City("beijing", "北京", "Beijing", true),
        City("shanghai", "上海", "Shanghai", true),
        City("guangzhou", "广州", "Guangzhou", true),
        City("hongkong", "香港", "Hongkong", true),
        City("shenzhen", "深圳", "Shenzhen", true),
        City("hangzhou", "杭州", "Hangzhou", true),
        City("chengdu", "成都", "Chengdu", true),
        City("nanjing", "南京", "Nanjing", true),
        City("chongqing", "重庆", "Chongqing", true),
        City("xian", "西安", "Xian", true),
        City("sanya", "三亚", "Sanya", true),
        City("suzhou", "苏州", "Suzhou", true),
        City("wuhan", "武汉", "Wuhan", true),
        City("macau", "澳门", "Macau", true),
        City("changsha", "长沙", "Changsha", true),
        City("xiamen", "厦门", "Xiamen", true),
        City("qingdao", "青岛", "Qingdao", true),
        City("tianjin", "天津", "Tianjin", true),
        City("jiuzhaigou", "九寨沟", "Jiuzhaigou", true),
        City("jinan", "济南", "Jinan", true),
        City("zhuhai", "珠海", "Zhuhai", true),
        City("harbin", "哈尔滨", "Harbin", true),
        City("kunming", "昆明", "Kunming", true),
        City("zhengzhou", "郑州", "Zhengzhou", true),
        
        // A开头的城市
        City("aomen", "澳门", "Aomen", false),
        City("abagaqi", "阿巴嘎旗", "Abagaqi", false),
        City("abaxian", "阿坝县", "Abaxian", false),
        City("aershan", "阿尔山", "Aershan", false),
    )
    
    override fun getCitySelectData(): CitySelectData {
        val historySearches = getHistorySearches()
        
        return CitySelectData(
            searchHint = "全球城市/区域/位置/酒店",
            isLocationEnabled = false,
            historySearches = historySearches,
            regionTabs = listOf(
                RegionTabCity("domestic", "国内(含港澳台)", true),
                RegionTabCity("overseas", "海外", false),
                RegionTabCity("beijing_hot", "北京热搜", false)
            ),
            hotCities = getHotCitiesByRegion("domestic"),
            alphabeticalCities = getAlphabeticalCities(),
            alphabetIndex = listOf("A", "B", "C", "D", "E", "F", "G", "H", "J", "K", "L", "M", "N", "P", "Q", "R", "S", "T", "W", "X", "Y", "Z")
        )
    }
    
    override fun searchCities(query: String): SearchResult {
        val filteredCities = allCities.filter { city ->
            city.name.contains(query, ignoreCase = true) || 
            city.pinyin.contains(query, ignoreCase = true)
        }
        
        return SearchResult(
            cities = filteredCities,
            searchTerm = query
        )
    }
    
    override fun addToHistory(cityName: String) {
        val currentHistory = getHistorySearches().toMutableList()
        
        // Remove if already exists to avoid duplicates
        currentHistory.remove(cityName)
        // Add to beginning
        currentHistory.add(0, cityName)
        // Keep only last 10 searches
        if (currentHistory.size > 10) {
            currentHistory.removeAt(currentHistory.size - 1)
        }
        
        val historyJson = gson.toJson(currentHistory)
        sharedPreferences.edit()
            .putString("search_history", historyJson)
            .apply()
    }
    
    override fun clearHistory() {
        sharedPreferences.edit()
            .remove("search_history")
            .apply()
    }
    
    override fun getHotCitiesByRegion(regionId: String): List<City> {
        return when (regionId) {
            "domestic" -> allCities.filter { it.isHot }
            "overseas" -> listOf(
                City("newyork", "纽约", "Newyork", true),
                City("london", "伦敦", "London", true),
                City("tokyo", "东京", "Tokyo", true),
                City("paris", "巴黎", "Paris", true)
            )
            "beijing_hot" -> listOf(
                City("beijing", "北京", "Beijing", true),
                City("tianjin", "天津", "Tianjin", true),
                City("hebei", "河北", "Hebei", true)
            )
            else -> emptyList()
        }
    }
    
    override fun getCitiesByAlphabet(letter: String): List<City> {
        return allCities.filter { city ->
            city.pinyin.uppercase().startsWith(letter.uppercase())
        }
    }
    
    override fun getAllCities(): List<City> {
        return allCities
    }
    
    override fun saveCitySelection(city: City) {
        addToHistory(city.name)
        
        // Save selected city action for AI analysis
        val actionData = mapOf(
            "action" to "city_selected",
            "timestamp" to System.currentTimeMillis(),
            "city_id" to city.id,
            "city_name" to city.name,
            "pinyin" to city.pinyin
        )
        
        val actionJson = gson.toJson(actionData)
        sharedPreferences.edit()
            .putString("last_city_selection_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    private fun getHistorySearches(): List<String> {
        val historyJson = sharedPreferences.getString("search_history", null)
        return if (historyJson != null) {
            val type = object : TypeToken<List<String>>() {}.type
            gson.fromJson(historyJson, type) ?: getDefaultHistory()
        } else {
            getDefaultHistory()
        }
    }
    
    private fun getDefaultHistory(): List<String> {
        return listOf("北京", "成都")
    }
    
    private fun getAlphabeticalCities(): Map<String, List<City>> {
        val result = mutableMapOf<String, MutableList<City>>()
        
        allCities.forEach { city ->
            val firstLetter = city.pinyin.first().uppercase().toString()
            if (!result.containsKey(firstLetter)) {
                result[firstLetter] = mutableListOf()
            }
            result[firstLetter]?.add(city)
        }
        
        // Sort cities within each letter group
        result.forEach { (_, cities) ->
            cities.sortBy { it.pinyin }
        }
        
        return result
    }
}