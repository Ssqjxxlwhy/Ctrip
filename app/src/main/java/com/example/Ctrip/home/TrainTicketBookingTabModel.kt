package com.example.Ctrip.home

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import java.time.LocalDate
import com.example.Ctrip.utils.DateUtils

interface TrainTicketBookingTabModel : TrainTicketBookingTabContract.Model

class TrainTicketBookingTabModelImpl(private val context: Context) : TrainTicketBookingTabModel {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("train_ticket_actions", Context.MODE_PRIVATE)
    private val gson = Gson()
    
    private var currentSearchParams = TrainSearchParams(
        departureCity = "上海",
        arrivalCity = "北京",
        departureDate = DateUtils.getTomorrowDate(),
        isMultiDayCommute = false,
        isStudentTicket = false,
        isHighSpeedTrain = false
    )
    
    override fun getTrainBookingData(): TrainTicketBookingData {
        return TrainTicketBookingData(
            promotionBanner = TrainPromotionBanner(
                title = "乐购阿克苏 旧貌换新颜",
                subtitle = "2023年南京苏陕协作交流消费节",
                backgroundColor = "#4A90E2"
            ),
            transportTabs = listOf(
                TransportTabTrain("flight", "机票", false, "✈️"),
                TransportTabTrain("train", "国内·国际火车", true, "🚄"),
                TransportTabTrain("bus", "汽车", false, "🚌"),
                TransportTabTrain("ship", "船票", false, "🚢")
            ),
            regionTabs = listOf(
                RegionTab("domestic", "国内", true),
                RegionTab("europe", "欧洲", false),
                RegionTab("japan_korea", "日韩", false),
                RegionTab("southeast_asia", "东南亚", false)
            ),
            tripTypeTabs = listOf(
                TripTypeTabTrain("oneway", "单程", true),
                TripTypeTabTrain("roundtrip", "往返", false)
            ),
            searchParams = currentSearchParams,
            newCustomerGift = NewCustomerGift(
                title = "送您一份新客礼包",
                value = "价值¥130",
                coupons = listOf(
                    CouponInfo("优惠券", "¥10立减", "饮食"),
                    CouponInfo("VIP抢票", "¥80", ""),
                    CouponInfo("安心退改", "¥25", ""),
                    CouponInfo("免费改签", "¥15", "")
                ),
                backgroundColor = "#FFE4E1"
            ),
            travelToolbox = TravelToolbox(
                title = "出行百宝箱",
                mainTools = listOf(
                    ToolboxItem("ticket_prediction", "放票预测", "查询全程放票时间", "🔮", "#E8F5E8"),
                    ToolboxItem("waiting_prediction", "候补预测", "结果早知道", "⏰", "#E1F3FF"),
                    ToolboxItem("destination_subsidy", "目的地补贴", "最高可免单", "💰", "#FFF3E0")
                ),
                additionalTools = listOf(
                    ToolboxItem("ticket_expert", "有票专家", "", "👨‍💼", "#FFFFFF"),
                    ToolboxItem("seat_change", "在线换座", "", "💺", "#FFFFFF"),
                    ToolboxItem("bus_ticket", "订汽车票", "", "🚌", "#FFFFFF"),
                    ToolboxItem("money_saving", "省钱任务", "", "💸", "#FFFFFF")
                )
            ),
            lowPriceDeals = listOf(
                TrainDeal("lowprice_train", "低价火车票", "", "#FFF8DC"),
                TrainDeal("fast_train", "火车小时达", "", "#E6F3FF")
            )
        )
    }
    
    override fun updateSearchParams(params: TrainSearchParams) {
        currentSearchParams = params
        saveSearchAction(params)
    }
    
    override fun saveSearchAction(params: TrainSearchParams) {
        val actionData = mapOf(
            "action" to "train_search",
            "timestamp" to System.currentTimeMillis(),
            "departure_city" to params.departureCity,
            "arrival_city" to params.arrivalCity,
            "departure_date" to params.departureDate.toString(),
            "multi_day_commute" to params.isMultiDayCommute,
            "student_ticket" to params.isStudentTicket,
            "high_speed_train" to params.isHighSpeedTrain
        )
        
        val actionJson = gson.toJson(actionData)
        sharedPreferences.edit()
            .putString("last_train_search_${System.currentTimeMillis()}", actionJson)
            .apply()
    }
    
    override fun clearSearchHistory() {
        val editor = sharedPreferences.edit()
        val allKeys = sharedPreferences.all.keys
        for (key in allKeys) {
            if (key.startsWith("last_train_search_")) {
                editor.remove(key)
            }
        }
        editor.apply()
    }
}