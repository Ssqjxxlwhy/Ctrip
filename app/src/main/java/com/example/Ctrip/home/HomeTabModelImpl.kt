package com.example.Ctrip.home

import android.content.Context
import com.example.Ctrip.utils.ClickHistoryManager
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader

class HomeTabModelImpl(private val context: Context) : HomeTabContract.Model {
    
    override fun getHomeData(): HomeTabData? {
        return try {
            val jsonString = loadJsonFromAssets("home_data.json")
            parseHomeData(jsonString)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
    
    override fun saveActionClick(actionId: String) {
        // Save action click for AI task checking
        println("Action clicked: $actionId")

        // 记录点击事件到 JSON 文件，供自动化测试使用
        when (actionId) {
            "hotel" -> ClickHistoryManager.recordClick(context, "酒店", "酒店预订页面")
            "flight" -> ClickHistoryManager.recordClick(context, "机票", "机票预订页面")
            "train" -> ClickHistoryManager.recordClick(context, "火车票", "火车票预订页面")
            else -> ClickHistoryManager.recordClick(context, actionId, "${actionId}页面")
        }
    }
    
    override fun saveSearchQuery(query: String) {
        // Save search query for AI task checking
        // For now, just log it
        println("Search query: $query")
    }
    
    private fun loadJsonFromAssets(fileName: String): String {
        return context.assets.open("data/$fileName").use { inputStream ->
            BufferedReader(InputStreamReader(inputStream)).use { reader ->
                reader.readText()
            }
        }
    }
    
    private fun parseHomeData(jsonString: String): HomeTabData {
        val jsonObject = JSONObject(jsonString)
        
        // Parse user info
        val userInfoJson = jsonObject.getJSONObject("userInfo")
        val userInfo = UserInfo(
            memberLevel = userInfoJson.getString("memberLevel"),
            points = userInfoJson.getInt("points")
        )
        
        // Parse main actions
        val mainActionsJson = jsonObject.getJSONArray("mainActions")
        val mainActions = mutableListOf<QuickAction>()
        for (i in 0 until mainActionsJson.length()) {
            val actionJson = mainActionsJson.getJSONObject(i)
            mainActions.add(
                QuickAction(
                    id = actionJson.getString("id"),
                    title = actionJson.getString("title"),
                    icon = actionJson.getString("icon"),
                    color = actionJson.getString("color")
                )
            )
        }
        
        // Parse secondary actions
        val secondaryActionsJson = jsonObject.getJSONArray("secondaryActions")
        val secondaryActions = mutableListOf<QuickAction>()
        for (i in 0 until secondaryActionsJson.length()) {
            val actionJson = secondaryActionsJson.getJSONObject(i)
            secondaryActions.add(
                QuickAction(
                    id = actionJson.getString("id"),
                    title = actionJson.getString("title"),
                    icon = actionJson.getString("icon"),
                    color = actionJson.getString("color")
                )
            )
        }
        
        // Parse search suggestions
        val suggestionsJson = jsonObject.getJSONArray("searchSuggestions")
        val searchSuggestions = mutableListOf<SearchSuggestion>()
        for (i in 0 until suggestionsJson.length()) {
            val suggestionJson = suggestionsJson.getJSONObject(i)
            searchSuggestions.add(
                SearchSuggestion(
                    text = suggestionJson.getString("text"),
                    type = suggestionJson.getString("type")
                )
            )
        }
        
        // Parse content sections
        val sectionsJson = jsonObject.getJSONArray("contentSections")
        val contentSections = mutableListOf<ContentSection>()
        for (i in 0 until sectionsJson.length()) {
            val sectionJson = sectionsJson.getJSONObject(i)
            contentSections.add(
                ContentSection(
                    id = sectionJson.getString("id"),
                    title = sectionJson.getString("title"),
                    icon = sectionJson.getString("icon")
                )
            )
        }
        
        // Parse promotion banners
        val bannersJson = jsonObject.getJSONArray("promotionBanners")
        val promotionBanners = mutableListOf<PromotionBanner>()
        for (i in 0 until bannersJson.length()) {
            val bannerJson = bannersJson.getJSONObject(i)
            promotionBanners.add(
                PromotionBanner(
                    id = bannerJson.getString("id"),
                    title = bannerJson.getString("title"),
                    subtitle = bannerJson.getString("subtitle"),
                    price = bannerJson.getString("price"),
                    imageUrl = bannerJson.getString("imageUrl"),
                    backgroundColor = bannerJson.getString("backgroundColor")
                )
            )
        }
        
        return HomeTabData(
            userInfo = userInfo,
            mainActions = mainActions,
            secondaryActions = secondaryActions,
            searchSuggestions = searchSuggestions,
            contentSections = contentSections,
            promotionBanners = promotionBanners
        )
    }
}