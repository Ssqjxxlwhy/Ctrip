package com.example.Ctrip.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 搜索参数管理器
 * 用于记录用户的搜索行为，便于自动化测试验证
 */
object SearchParamsManager {
    private const val FILE_NAME = "search_params.json"

    /**
     * 记录酒店搜索参数
     */
    fun recordHotelSearch(
        context: Context,
        city: String? = null,
        checkIn: String? = null,
        checkOut: String? = null,
        rooms: Int? = null,
        adults: Int? = null,
        children: Int? = null,
        sortBy: String? = null,
        limit: Int? = null,
        listShown: Boolean = false
    ) {
        val params = mutableMapOf<String, Any>("type" to "hotel_search")
        city?.let { params["city"] = it }
        checkIn?.let { params["checkIn"] = it }
        checkOut?.let { params["checkOut"] = it }
        rooms?.let { params["rooms"] = it }
        adults?.let { params["adults"] = it }
        children?.let { params["children"] = it }
        sortBy?.let { params["sortBy"] = it }
        limit?.let { params["limit"] = it }
        params["list_shown"] = listShown

        recordSearch(context, params)
    }

    /**
     * 记录机票搜索参数
     */
    fun recordFlightSearch(
        context: Context,
        from: String? = null,
        to: String? = null,
        date: String? = null,
        cabin: String? = null,
        calculation: String? = null,
        listShown: Boolean = false
    ) {
        val params = mutableMapOf<String, Any>("type" to "flight_search")
        from?.let { params["from"] = it }
        to?.let { params["to"] = it }
        date?.let { params["date"] = it }
        cabin?.let { params["cabin"] = it }
        calculation?.let { params["calculation"] = it }
        params["list_shown"] = listShown

        recordSearch(context, params)
    }

    /**
     * 记录火车票搜索参数
     */
    fun recordTrainSearch(
        context: Context,
        from: String? = null,
        to: String? = null,
        date: String? = null,
        ticketType: String? = null,
        timeRange: String? = null,
        listShown: Boolean = false
    ) {
        val params = mutableMapOf<String, Any>("type" to "train_search")
        from?.let { params["from"] = it }
        to?.let { params["to"] = it }
        date?.let { params["date"] = it }
        ticketType?.let { params["ticketType"] = it }
        timeRange?.let { params["timeRange"] = it }
        params["list_shown"] = listShown

        recordSearch(context, params)
    }

    /**
     * 通用搜索记录方法
     */
    private fun recordSearch(context: Context, params: Map<String, Any>) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            val currentTime = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(Date())

            // 读取现有数据
            val jsonData = if (file.exists()) {
                val content = file.readText()
                if (content.isNotEmpty()) JSONObject(content) else JSONObject()
            } else {
                JSONObject()
            }

            // 获取或创建 search_events 数组
            val searchEvents = if (jsonData.has("search_events")) {
                jsonData.getJSONArray("search_events")
            } else {
                JSONArray()
            }

            // 创建新的搜索事件
            val newEvent = JSONObject().apply {
                put("time", currentTime)
                params.forEach { (key, value) ->
                    put(key, value)
                }
            }

            // 添加到数组
            searchEvents.put(newEvent)

            // 更新数据
            jsonData.put("search_events", searchEvents)

            // 写入文件
            file.writeText(jsonData.toString(2))

            android.util.Log.d("SearchParams", "记录搜索: ${params["type"]}, params=$params")
        } catch (e: Exception) {
            android.util.Log.e("SearchParams", "记录搜索失败", e)
        }
    }

    /**
     * 清空搜索历史
     */
    fun clearHistory(context: Context) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            android.util.Log.e("SearchParams", "清空历史失败", e)
        }
    }

    /**
     * 获取搜索历史
     */
    fun getHistory(context: Context): String {
        return try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) file.readText() else "{}"
        } catch (e: Exception) {
            "{}"
        }
    }
}
