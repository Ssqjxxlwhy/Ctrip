package com.example.Ctrip.home

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

class TrainDetailTabModel(private val context: Context) : TrainDetailTabContract.Model {

    private val gson = Gson()
    private var cachedTrains: List<TrainTicket>? = null

    override fun getTrainDetail(trainId: String): TrainDetailData? {
        val train = loadTrainsFromAssets().find { it.trainId == trainId } ?: return null

        return TrainDetailData(
            trainId = train.trainId,
            trainNumber = train.trainNumber,
            trainType = train.trainType,
            departureDate = formatDate(train.departureDate),
            departureTime = train.departureTime,
            arrivalTime = train.arrivalTime,
            departureStation = train.departureStation,
            arrivalStation = train.arrivalStation,
            duration = train.duration,
            supports12306Points = train.trainNumber.startsWith("G") || train.trainNumber.startsWith("D"),
            hasRefundPolicy = true,
            isHighSpeed = train.trainType.contains("复兴号") || train.trainType.contains("动车"),
            seatTypes = getSeatTypes(trainId, "二等座"),
            ticketOptions = getTicketOptions(trainId, "二等座")
        )
    }

    override fun getSeatTypes(trainId: String, selectedSeatType: String): List<SeatType> {
        val train = loadTrainsFromAssets().find { it.trainId == trainId } ?: return emptyList()

        val seatTypes = mutableListOf<SeatType>()

        // 二等座
        if (train.availableSeats.containsKey("二等座")) {
            val available = train.availableSeats["二等座"] ?: 0
            val price = train.prices["二等座"]?.toInt() ?: 0
            seatTypes.add(
                SeatType(
                    type = "二等座",
                    discount = "8.7折",
                    price = price,
                    availableCount = available,
                    isSelected = selectedSeatType == "二等座"
                )
            )
        }

        // 一等座
        if (train.availableSeats.containsKey("一等座")) {
            val available = train.availableSeats["一等座"] ?: 0
            val price = train.prices["一等座"]?.toInt() ?: 0
            seatTypes.add(
                SeatType(
                    type = "一等座",
                    discount = "9.2折",
                    price = price,
                    availableCount = available,
                    isSelected = selectedSeatType == "一等座"
                )
            )
        }

        // 商务座
        if (train.availableSeats.containsKey("商务座")) {
            val available = train.availableSeats["商务座"] ?: 0
            val price = train.prices["商务座"]?.toInt() ?: 0
            seatTypes.add(
                SeatType(
                    type = "商务座",
                    discount = "8.1折",
                    price = price,
                    availableCount = available,
                    isSelected = selectedSeatType == "商务座"
                )
            )
        }

        // 无座
        seatTypes.add(
            SeatType(
                type = "无座",
                discount = "",
                price = train.prices["二等座"]?.toInt() ?: 0,
                availableCount = 99,
                isSelected = selectedSeatType == "无座"
            )
        )

        return seatTypes
    }

    override fun getTicketOptions(trainId: String, seatType: String): List<TrainTicketOption> {
        val train = loadTrainsFromAssets().find { it.trainId == trainId } ?: return emptyList()
        val basePrice = train.prices[seatType]?.toInt() ?: train.prices["二等座"]?.toInt() ?: 0
        val availableCount = train.availableSeats[seatType] ?: train.availableSeats["二等座"] ?: 0

        return listOf(
            TrainTicketOption(
                id = "option_1",
                price = basePrice - 2,
                platformDiscount = "¥10",
                subsidy = "¥2",
                insurancePrice = "+¥52携程全能保障",
                benefits = listOf(
                    "退改费补偿",
                    "视频音乐会员5选1",
                    "北京旅行好物3选1",
                    "订酒店返现¥25  共7项"
                ),
                availableCount = availableCount
            ),
            TrainTicketOption(
                id = "option_2",
                price = basePrice + 2,
                platformDiscount = "¥10",
                subsidy = "",
                insurancePrice = "+¥30优享预订",
                benefits = listOf(
                    "全天随时可出票",
                    "免登录12306账号"
                ),
                availableCount = availableCount
            ),
            TrainTicketOption(
                id = "option_3",
                price = basePrice + 2,
                platformDiscount = "¥10",
                subsidy = "",
                insurancePrice = "",
                benefits = listOf(
                    "12306免费购票",
                    "需登录12306账号"
                ),
                availableCount = availableCount
            ),
            TrainTicketOption(
                id = "option_4",
                price = basePrice - 25,
                platformDiscount = "",
                subsidy = "",
                insurancePrice = "",
                benefits = listOf(
                    "下单后预订酒店，本单返现¥25"
                ),
                availableCount = availableCount,
                specialTag = "返现预订"
            )
        )
    }

    private fun loadTrainsFromAssets(): List<TrainTicket> {
        if (cachedTrains != null) {
            return cachedTrains!!
        }

        return try {
            val inputStream = context.assets.open("data/trains_list.json")
            val reader = InputStreamReader(inputStream)
            val type = object : TypeToken<List<TrainTicket>>() {}.type
            val trains: List<TrainTicket> = gson.fromJson(reader, type)
            cachedTrains = trains
            reader.close()
            trains
        } catch (e: Exception) {
            emptyList()
        }
    }

    private fun formatDate(dateStr: String): String {
        return try {
            val date = LocalDate.parse(dateStr, DateTimeFormatter.ISO_DATE)
            val dayOfWeek = when (date.dayOfWeek.value) {
                1 -> "周一"
                2 -> "周二"
                3 -> "周三"
                4 -> "周四"
                5 -> "周五"
                6 -> "周六"
                7 -> "周日"
                else -> ""
            }
            "${date.monthValue}月${date.dayOfMonth}日 $dayOfWeek"
        } catch (e: Exception) {
            dateStr
        }
    }
}
