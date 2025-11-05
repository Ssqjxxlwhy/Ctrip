package com.example.Ctrip.utils

import android.content.Context
import org.json.JSONArray
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

/**
 * 预订历史管理器
 * 用于记录用户的预订行为，便于自动化测试验证
 */
object BookingHistoryManager {
    private const val FILE_NAME = "booking_history.json"

    /**
     * 记录酒店预订
     */
    fun recordHotelBooking(
        context: Context,
        city: String,
        checkIn: String,
        checkOut: String,
        hotelIndex: Int? = null,
        roomIndex: Int? = null,
        selection: String? = null,
        hotelName: String? = null,
        price: Double? = null
    ) {
        val params = mutableMapOf<String, Any>(
            "type" to "hotel_booking",
            "city" to city,
            "checkIn" to checkIn,
            "checkOut" to checkOut
        )
        hotelIndex?.let { params["hotelIndex"] = it }
        roomIndex?.let { params["roomIndex"] = it }
        selection?.let { params["selection"] = it }
        hotelName?.let { params["hotelName"] = it }
        price?.let { params["price"] = it }

        recordBooking(context, params)
    }

    /**
     * 记录机票预订
     */
    fun recordFlightBooking(
        context: Context,
        from: String,
        to: String,
        date: String,
        flightIndex: Int? = null,
        cabin: String? = null,
        price: Double? = null
    ) {
        val params = mutableMapOf<String, Any>(
            "type" to "flight_booking",
            "from" to from,
            "to" to to,
            "date" to date
        )
        flightIndex?.let { params["flightIndex"] = it }
        cabin?.let { params["cabin"] = it }
        price?.let { params["price"] = it }

        recordBooking(context, params)
    }

    /**
     * 记录火车票预订
     */
    fun recordTrainBooking(
        context: Context,
        from: String,
        to: String,
        date: String,
        trainIndex: Int? = null,
        departureTime: String? = null,
        arrivalTime: String? = null,
        duration: String? = null,
        constraint: String? = null,
        price: Double? = null
    ) {
        val params = mutableMapOf<String, Any>(
            "type" to "train_booking",
            "from" to from,
            "to" to to,
            "date" to date
        )
        trainIndex?.let { params["trainIndex"] = it }
        departureTime?.let { params["departureTime"] = it }
        arrivalTime?.let { params["arrivalTime"] = it }
        duration?.let { params["duration"] = it }
        constraint?.let { params["constraint"] = it }
        price?.let { params["price"] = it }

        recordBooking(context, params)
    }

    /**
     * 记录多步骤预订（用于复杂任务）
     */
    fun recordMultiStepBooking(
        context: Context,
        steps: List<Map<String, Any>>,
        totalCost: Double? = null,
        budgetCheck: Int? = null
    ) {
        val params = mutableMapOf<String, Any>(
            "type" to "multi_booking",
            "steps" to JSONArray(steps)
        )
        totalCost?.let { params["totalCost"] = it }
        budgetCheck?.let { params["budgetCheck"] = it }

        recordBooking(context, params)
    }

    /**
     * 记录批量预订
     */
    fun recordBatchBooking(
        context: Context,
        bookingType: String,
        from: String? = null,
        to: String? = null,
        city: String? = null,
        date: String? = null,
        quantity: Int,
        constraint: String? = null,
        cabin: String? = null
    ) {
        val params = mutableMapOf<String, Any>(
            "type" to "batch_booking",
            "bookingType" to bookingType,
            "quantity" to quantity
        )
        from?.let { params["from"] = it }
        to?.let { params["to"] = it }
        city?.let { params["city"] = it }
        date?.let { params["date"] = it }
        constraint?.let { params["constraint"] = it }
        cabin?.let { params["cabin"] = it }

        recordBooking(context, params)
    }

    /**
     * 通用预订记录方法
     */
    private fun recordBooking(context: Context, params: Map<String, Any>) {
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

            // 获取或创建 booking_events 数组
            val bookingEvents = if (jsonData.has("booking_events")) {
                jsonData.getJSONArray("booking_events")
            } else {
                JSONArray()
            }

            // 创建新的预订事件
            val newEvent = JSONObject().apply {
                put("time", currentTime)
                params.forEach { (key, value) ->
                    put(key, value)
                }
            }

            // 添加到数组
            bookingEvents.put(newEvent)

            // 更新数据
            jsonData.put("booking_events", bookingEvents)

            // 写入文件
            file.writeText(jsonData.toString(2))

            android.util.Log.d("BookingHistory", "记录预订: ${params["type"]}, params=$params")
        } catch (e: Exception) {
            android.util.Log.e("BookingHistory", "记录预订失败", e)
        }
    }

    /**
     * 清空预订历史
     */
    fun clearHistory(context: Context) {
        try {
            val file = File(context.filesDir, FILE_NAME)
            if (file.exists()) {
                file.delete()
            }
        } catch (e: Exception) {
            android.util.Log.e("BookingHistory", "清空历史失败", e)
        }
    }

    /**
     * 获取预订历史
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
