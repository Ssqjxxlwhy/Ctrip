package com.example.Ctrip.home

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.io.InputStreamReader
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class TrainMateListTabModel(private val context: Context) : TrainMateListTabContract.Model {

    private val gson = Gson()
    private var cachedTrains: List<TrainTicket>? = null

    override fun getTrainList(departureCity: String, arrivalCity: String, departureDate: LocalDate): List<TrainTicket> {
        val allTrains = loadTrainsFromAssets()
        val dateStr = departureDate.format(DateTimeFormatter.ISO_DATE)

        android.util.Log.d("TrainMateListTabModel", "getTrainList called with: departureCity=$departureCity, arrivalCity=$arrivalCity, dateStr=$dateStr")
        android.util.Log.d("TrainMateListTabModel", "Total trains loaded: ${allTrains.size}")

        // 根据搜索条件筛选火车票
        val result = allTrains.filter { train ->
            train.departureCity == departureCity &&
            train.arrivalCity == arrivalCity &&
            train.departureDate == dateStr
        }

        android.util.Log.d("TrainMateListTabModel", "Filtered trains count: ${result.size}")
        if (result.isEmpty()) {
            android.util.Log.d("TrainMateListTabModel", "Sample train data (first 3):")
            allTrains.take(3).forEach { train ->
                android.util.Log.d("TrainMateListTabModel", "  Train: ${train.departureCity} -> ${train.arrivalCity} on ${train.departureDate}")
            }
        }

        return result
    }

    override fun filterTrains(trains: List<TrainTicket>, filterOptions: TrainFilterOptions): List<TrainTicket> {
        var filteredTrains = trains

        // 按出发站筛选
        if (filterOptions.departureStation != null) {
            filteredTrains = filteredTrains.filter { it.departureStation == filterOptions.departureStation }
        }

        // 按到达站筛选
        if (filterOptions.arrivalStation != null) {
            filteredTrains = filteredTrains.filter { it.arrivalStation == filterOptions.arrivalStation }
        }

        // 仅看有票
        if (filterOptions.onlyWithTickets) {
            filteredTrains = filteredTrains.filter { train ->
                train.availableSeats.values.any { it > 0 }
            }
        }

        // 仅看直达
        if (filterOptions.onlyDirect) {
            filteredTrains = filteredTrains.filter { train ->
                train.features.any { it.contains("直达") }
            }
        }

        return filteredTrains
    }

    override fun sortTrains(trains: List<TrainTicket>, sortType: SortType): List<TrainTicket> {
        return when (sortType) {
            SortType.EARLIEST_DEPARTURE -> trains.sortedBy { it.departureTime }
            SortType.SHORTEST_DURATION -> trains.sortedBy { parseDuration(it.duration) }
            SortType.LOWEST_PRICE -> trains.sortedBy { getLowestPrice(it.prices) }
            SortType.DEFAULT -> trains
        }
    }

    override fun getDateOptions(selectedDate: LocalDate): List<TrainDateOption> {
        val today = LocalDate.now()
        val options = mutableListOf<TrainDateOption>()

        for (i in 0..4) {
            val date = today.plusDays(i.toLong())
            val displayText = when (i) {
                0 -> "今天${date.dayOfMonth}"
                1 -> "明天${date.monthValue}-${date.dayOfMonth}"
                else -> {
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
                    dayOfWeek
                }
            }
            val subText = if (i > 1) "${date.dayOfMonth}" else ""
            options.add(TrainDateOption(date, displayText, subText, date == selectedDate))
        }

        return options
    }

    override fun getTransportOptions(): List<TransportOption> {
        return listOf(
            TransportOption("train", "火车", "152.5元起", true),
            TransportOption("flight", "飞机", "410元起", false),
            TransportOption("transfer", "智能中转", "", false)
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

    private fun parseDuration(duration: String): Int {
        // 解析时长，如 "6时55分" -> 415分钟
        val hourRegex = "(\\d+)时".toRegex()
        val minuteRegex = "(\\d+)分".toRegex()

        val hours = hourRegex.find(duration)?.groupValues?.get(1)?.toIntOrNull() ?: 0
        val minutes = minuteRegex.find(duration)?.groupValues?.get(1)?.toIntOrNull() ?: 0

        return hours * 60 + minutes
    }

    private fun getLowestPrice(prices: Map<String, Double>): Double {
        return prices.values.filter { it > 0 }.minOrNull() ?: Double.MAX_VALUE
    }
}
