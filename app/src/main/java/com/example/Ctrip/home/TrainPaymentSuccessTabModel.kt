package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.example.Ctrip.utils.BookingHistoryManager
import org.json.JSONObject

class TrainPaymentSuccessTabModel(private val context: Context) {

    private val trainListPreferences: SharedPreferences =
        context.getSharedPreferences("train_list_data", Context.MODE_PRIVATE)

    fun getPaymentSuccessData(paymentData: TicketPaymentTabContract.PaymentData): TrainPaymentSuccessTabContract.PaymentSuccessData {
        // 保存火车票预订信息到booking_history.json
        saveTrainBookingHistory()

        return TrainPaymentSuccessTabContract.PaymentSuccessData(
            orderId = paymentData.orderId,
            paymentAmount = paymentData.finalPrice,
            paymentMethod = "微信支付",
            trainNumber = paymentData.trainNumber,
            departureStation = paymentData.departureStation,
            arrivalStation = paymentData.arrivalStation,
            departureDate = paymentData.departureDate,
            departureTime = paymentData.departureTime,
            passengerName = paymentData.passengerName,
            seatType = paymentData.seatType
        )
    }

    /**
     * 保存火车票预订信息到booking_history.json
     */
    private fun saveTrainBookingHistory() {
        try {
            // 从SharedPreferences读取当前预订的车次信息
            val bookingInfoJson = trainListPreferences.getString("current_train_booking_info", null)
            if (bookingInfoJson != null) {
                val jsonObject = JSONObject(bookingInfoJson)

                // 提取预订信息
                val from = jsonObject.optString("from")
                val to = jsonObject.optString("to")
                val date = jsonObject.optString("date")
                val trainIndex = jsonObject.optInt("trainIndex", -1)
                val departureTime = jsonObject.optString("departureTime", "")
                val arrivalTime = jsonObject.optString("arrivalTime", "")
                val duration = jsonObject.optString("duration", "")
                val price = jsonObject.optDouble("price", 0.0)

                // 验证数据完整性
                if (from.isNotEmpty() && to.isNotEmpty() && date.isNotEmpty() && trainIndex >= 0) {
                    // 调用BookingHistoryManager保存预订记录
                    BookingHistoryManager.recordTrainBooking(
                        context = context,
                        from = from,
                        to = to,
                        date = date,
                        trainIndex = trainIndex,
                        departureTime = if (departureTime.isNotEmpty()) departureTime else null,
                        arrivalTime = if (arrivalTime.isNotEmpty()) arrivalTime else null,
                        duration = if (duration.isNotEmpty()) duration else null,
                        price = if (price > 0) price else null
                    )
                    android.util.Log.d("TrainPaymentSuccess", "火车票预订记录已保存: $from -> $to, $date, 车次索引=$trainIndex, 出发时间=$departureTime, 到达时间=$arrivalTime, 时长=$duration, 价格=$price")
                } else {
                    android.util.Log.e("TrainPaymentSuccess", "预订信息不完整: from=$from, to=$to, date=$date, trainIndex=$trainIndex")
                }
            } else {
                android.util.Log.e("TrainPaymentSuccess", "未找到当前预订的车次信息")
            }
        } catch (e: Exception) {
            android.util.Log.e("TrainPaymentSuccess", "保存火车票预订历史失败", e)
        }
    }
}
