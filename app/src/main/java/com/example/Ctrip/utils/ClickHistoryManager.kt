package com.example.Ctrip.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 点击历史记录管理器
 * 用于记录用户的点击行为，便于自动化测试验证
 */
object ClickHistoryManager {
    private const val FILE_NAME = "click_history.json"

    /**
     * 记录点击事件
     * @param context 上下文
     * @param icon 点击的图标名称
     * @param page 跳转到的页面名称
     */
    fun recordClick(context: Context, icon: String, page: String) {
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

            // 获取或创建 click_events 数组
            val clickEvents = if (jsonData.has("click_events")) {
                jsonData.getJSONArray("click_events")
            } else {
                JSONArray()
            }

            // 创建新的点击事件
            val newEvent = JSONObject().apply {
                put("time", currentTime)
                put("icon", icon)
                put("page", page)
            }

            // 添加到数组
            clickEvents.put(newEvent)

            // 更新数据
            jsonData.put("click_events", clickEvents)

            // 写入文件
            file.writeText(jsonData.toString(2))

            android.util.Log.d("ClickHistory", "记录点击: icon=$icon, page=$page, time=$currentTime")
        } catch (e: Exception) {
            android.util.Log.e("ClickHistory", "记录点击失败", e)
        }
    }

    /**
     * 清空点击历史
     */
    fun clearHistory(context: Context) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            android.util.Log.e("ClickHistory", "清空历史失败", e)
        }
    }

    /**
     * 获取点击历史
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
