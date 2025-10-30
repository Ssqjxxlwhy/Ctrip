package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.io.InputStreamReader

class InfoConfirmTabModel(private val context: Context) : InfoConfirmTabContract.Model {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("train_order_actions", Context.MODE_PRIVATE)
    private val gson = Gson()

    override fun getConfirmData(
        trainId: String,
        ticketOption: TrainTicketOption,
        selectedSeatType: String
    ): InfoConfirmData? {
        // 从trains_list.json加载火车信息
        val train = loadTrainById(trainId) ?: return null

        return InfoConfirmData(
            trainNumber = train.trainNumber,
            departureCity = train.departureCity,
            arrivalCity = train.arrivalCity,
            departureStation = train.departureStation,
            arrivalStation = train.arrivalStation,
            departureDate = formatDate(train.departureDate),
            departureTime = train.departureTime,
            arrivalTime = train.arrivalTime,
            duration = train.duration,
            seatType = selectedSeatType,
            ticketPrice = train.prices[selectedSeatType]?.toInt() ?: 0,
            insuranceName = parseInsuranceName(ticketOption.insurancePrice),
            insurancePrice = parseInsurancePrice(ticketOption.insurancePrice),
            currentUser = getCurrentUser(),
            availableCarriages = getCarriages(),
            availableSeats = getSeatOptions(),
            newCustomerDiscount = 10,
            points = 2880,
            pointsValue = 28.8
        )
    }

    override fun getCurrentUser(): TrainPassenger {
        // 返回预设的"我"的个人信息
        return TrainPassenger(
            name = "张伟",
            passengerType = PassengerType.ADULT,
            idType = IDType.ID_CARD,
            idNumber = "3201************234",
            phone = "138****5678"
        )
    }

    override fun getCarriages(): List<CarriageInfo> {
        return listOf(
            CarriageInfo("01", "", false),
            CarriageInfo("02", "", false),
            CarriageInfo("03", "", false),
            CarriageInfo("04", "出站快", false),
            CarriageInfo("05", "餐车", false),
            CarriageInfo("06", "就餐近", false),
            CarriageInfo("07", "", false),
            CarriageInfo("08", "", false)
        )
    }

    override fun getSeatOptions(): List<SeatSelectionInfo> {
        return listOf(
            SeatSelectionInfo(
                SeatPosition.WINDOW,
                "A/F",
                "靠窗",
                12,
                false
            ),
            SeatSelectionInfo(
                SeatPosition.AISLE,
                "C/D",
                "过道",
                12,
                false
            ),
            SeatSelectionInfo(
                SeatPosition.NEAR_DOOR,
                "靠车门",
                "上下车更方便",
                12,
                false
            ),
            SeatSelectionInfo(
                SeatPosition.FAR_DOOR,
                "远离车门",
                "远离卫生间",
                12,
                false
            )
        )
    }

    override fun saveOrderAction(data: InfoConfirmData) {
        val actionData = mapOf(
            "action" to "train_order_confirm",
            "timestamp" to System.currentTimeMillis(),
            "train_number" to data.trainNumber,
            "departure_city" to data.departureCity,
            "arrival_city" to data.arrivalCity,
            "departure_date" to data.departureDate,
            "departure_time" to data.departureTime,
            "seat_type" to data.seatType,
            "ticket_price" to data.ticketPrice,
            "insurance_name" to data.insuranceName,
            "insurance_price" to data.insurancePrice,
            "passenger_name" to data.currentUser.name,
            "passenger_phone" to data.currentUser.phone
        )

        val actionJson = gson.toJson(actionData)
        sharedPreferences.edit()
            .putString("train_order_${System.currentTimeMillis()}", actionJson)
            .apply()
    }

    private fun loadTrainById(trainId: String): TrainTicket? {
        return try {
            val inputStream = context.assets.open("data/trains_list.json")
            val reader = InputStreamReader(inputStream)
            val type = object : com.google.gson.reflect.TypeToken<List<TrainTicket>>() {}.type
            val trains: List<TrainTicket> = gson.fromJson(reader, type)
            reader.close()
            trains.find { it.trainId == trainId }
        } catch (e: Exception) {
            android.util.Log.e("InfoConfirmTabModel", "Error loading train: ${e.message}")
            null
        }
    }

    private fun formatDate(dateStr: String): String {
        // 将 "2025-10-30" 转换为 "10-30"
        val parts = dateStr.split("-")
        return if (parts.size == 3) {
            "${parts[1]}-${parts[2]}"
        } else {
            dateStr
        }
    }

    private fun parseInsuranceName(insurancePrice: String): String {
        // 从 "+¥52携程全能保障" 中提取保险名称
        val regex = "[\u4e00-\u9fa5]+".toRegex()
        val match = regex.find(insurancePrice)
        return match?.value ?: "携程全能保障"
    }

    private fun parseInsurancePrice(insurancePrice: String): Int {
        // 从 "+¥52携程全能保障" 中提取价格
        val regex = "\\d+".toRegex()
        val match = regex.find(insurancePrice)
        return match?.value?.toInt() ?: 52
    }
}
