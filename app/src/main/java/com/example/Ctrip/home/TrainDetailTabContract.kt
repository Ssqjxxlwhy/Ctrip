package com.example.Ctrip.home

// 座位类型
data class SeatType(
    val type: String,           // 二等座、一等座、商务座、无座
    val discount: String,       // 折扣信息，如"8.7折"
    val price: Int,             // 价格
    val availableCount: Int,    // 余票数量
    val isSelected: Boolean     // 是否选中
)

// 购票选项
data class TrainTicketOption(
    val id: String,
    val price: Int,                     // 价格
    val platformDiscount: String,       // 平台立减，如"¥10"
    val subsidy: String,                // 已补贴，如"¥2"
    val insurancePrice: String,         // 保险价格，如"+¥52携程全能保障"
    val benefits: List<String>,         // 服务权益列表
    val availableCount: Int,            // 剩余票数
    val specialTag: String = ""         // 特殊标签，如"返现预订"
)

// 火车详情数据
data class TrainDetailData(
    val trainId: String,
    val trainNumber: String,
    val trainType: String,
    val departureDate: String,          // 出发日期，如"10月30日 周四"
    val departureTime: String,
    val arrivalTime: String,
    val departureStation: String,
    val arrivalStation: String,
    val duration: String,
    val supports12306Points: Boolean,   // 支持12306积分兑换
    val hasRefundPolicy: Boolean,       // 退改说明
    val isHighSpeed: Boolean,           // 复兴号
    val seatTypes: List<SeatType>,
    val ticketOptions: List<TrainTicketOption>
)

interface TrainDetailTabContract {

    interface View {
        fun showTrainDetail(data: TrainDetailData)
        fun showLoading()
        fun hideLoading()
        fun showError(message: String)
        fun navigateBack()
        fun showRefundPolicy()
        fun showNotice()
        fun share()
        fun navigateToOrderConfirm(ticketOption: TrainTicketOption)
    }

    interface Presenter {
        fun attachView(view: View)
        fun detachView()
        fun loadTrainDetail(trainId: String)
        fun onSeatTypeSelected(seatType: String)
        fun onTicketOptionClicked(optionId: String)
        fun onBackClicked()
        fun onRefundPolicyClicked()
        fun onNoticeClicked()
        fun onShareClicked()
    }

    interface Model {
        fun getTrainDetail(trainId: String): TrainDetailData?
        fun getSeatTypes(trainId: String, seatType: String): List<SeatType>
        fun getTicketOptions(trainId: String, seatType: String): List<TrainTicketOption>
    }
}
